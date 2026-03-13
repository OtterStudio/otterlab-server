package OtterStudio.backend_api.global.security.oauth2;

import OtterStudio.backend_api.domain.user.entity.SocialProvider;
import OtterStudio.backend_api.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class OAuthAttributes {

    private String socialId;
    private String nickname;
    private SocialProvider provider;

    public static OAuthAttributes of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "kakao" -> ofKakao(attributes);
            case "google" -> ofGoogle(attributes);
            case "naver" -> ofNaver(attributes);
            default -> throw new IllegalArgumentException("Unsupported provider: " + registrationId);
        };
    }

    private static OAuthAttributes ofKakao(Map<String, Object> attributes) {
        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        @SuppressWarnings("unchecked")
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthAttributes.builder()
                .socialId(String.valueOf(attributes.get("id")))
                .nickname((String) profile.get("nickname"))
                .provider(SocialProvider.KAKAO)
                .build();
    }

    private static OAuthAttributes ofGoogle(Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .socialId((String) attributes.get("sub"))
                .nickname((String) attributes.get("name"))
                .provider(SocialProvider.GOOGLE)
                .build();
    }

    private static OAuthAttributes ofNaver(Map<String, Object> attributes) {
        @SuppressWarnings("unchecked")
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .socialId((String) response.get("id"))
                .nickname((String) response.get("nickname"))
                .provider(SocialProvider.NAVER)
                .build();
    }

    public User toUser() {
        return User.builder()
                .socialId(socialId)
                .provider(provider)
                .nickname(nickname)
                .build();
    }
}
