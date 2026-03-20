package OtterStudio.backend_api.global.security.oauth;

import OtterStudio.backend_api.domain.user.entity.SocialProvider;
import OtterStudio.backend_api.global.common.execption.BusinessException;
import OtterStudio.backend_api.global.common.execption.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
public class KakaoOAuthProvider implements OAuthProvider {

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String tokenUri;
    private final String userInfoUri;
    private final RestClient restClient;

    public KakaoOAuthProvider(
            @Value("${oauth2.kakao.client-id}") String clientId,
            @Value("${oauth2.kakao.client-secret}") String clientSecret,
            @Value("${oauth2.kakao.redirect-uri}") String redirectUri,
            @Value("${oauth2.kakao.token-uri}") String tokenUri,
            @Value("${oauth2.kakao.user-info-uri}") String userInfoUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.tokenUri = tokenUri;
        this.userInfoUri = userInfoUri;
        this.restClient = RestClient.create();
    }

    @Override
    public SocialProvider getProvider() {
        return SocialProvider.KAKAO;
    }

    @Override
    public OAuthUserInfo getUserInfo(String authorizationCode) {
        String accessToken = requestAccessToken(authorizationCode);
        return requestUserInfo(accessToken);
    }

    @SuppressWarnings("unchecked")
    private String requestAccessToken(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        try {
            Map<String, Object> response = restClient.post()
                    .uri(tokenUri)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(params)
                    .retrieve()
                    .body(Map.class);

            return (String) response.get("access_token");
        } catch (Exception e) {
            log.error("Kakao 토큰 요청 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OAUTH_AUTHENTICATION_FAILED);
        }
    }

    @SuppressWarnings("unchecked")
    private OAuthUserInfo requestUserInfo(String accessToken) {
        try {
            Map<String, Object> response = restClient.get()
                    .uri(userInfoUri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .body(Map.class);

            Map<String, Object> kakaoAccount = (Map<String, Object>) response.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            return OAuthUserInfo.builder()
                    .socialId(String.valueOf(response.get("id")))
                    .email((String) kakaoAccount.get("email"))
                    .nickname((String) profile.get("nickname"))
                    .provider(SocialProvider.KAKAO)
                    .build();
        } catch (Exception e) {
            log.error("Kakao 유저 정보 요청 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OAUTH_AUTHENTICATION_FAILED);
        }
    }
}
