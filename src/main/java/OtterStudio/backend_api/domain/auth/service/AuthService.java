package OtterStudio.backend_api.domain.auth.service;

import OtterStudio.backend_api.domain.auth.dto.AuthResponse;
import OtterStudio.backend_api.domain.auth.dto.UserInfoResponse;
import OtterStudio.backend_api.domain.user.entity.SocialProvider;
import OtterStudio.backend_api.domain.user.entity.User;
import OtterStudio.backend_api.domain.user.repository.UserRepository;
import OtterStudio.backend_api.global.common.execption.BusinessException;
import OtterStudio.backend_api.global.common.execption.ErrorCode;
import OtterStudio.backend_api.global.security.jwt.JwtTokenProvider;
import OtterStudio.backend_api.global.security.oauth.OAuthProvider;
import OtterStudio.backend_api.global.security.oauth.OAuthUserInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final Map<SocialProvider, OAuthProvider> oAuthProviderMap;

    public AuthService(UserRepository userRepository,
                       JwtTokenProvider jwtTokenProvider,
                       List<OAuthProvider> oAuthProviders) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.oAuthProviderMap = oAuthProviders.stream()
                .collect(Collectors.toMap(OAuthProvider::getProvider, Function.identity()));
    }

    @Transactional
    public AuthResponse socialLogin(SocialProvider provider, String authorizationCode) {
        OAuthProvider oAuthProvider = oAuthProviderMap.get(provider);
        if (oAuthProvider == null) {
            throw new BusinessException(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER);
        }

        OAuthUserInfo userInfo = oAuthProvider.getUserInfo(authorizationCode);
        User user = findOrCreateUser(userInfo);

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .nickname(user.getNickname())
                .build();
    }

    public AuthResponse refresh(String refreshToken) {
        jwtTokenProvider.validateToken(refreshToken);
        Long userId = jwtTokenProvider.getUserId(refreshToken);
        jwtTokenProvider.validateRefreshToken(refreshToken, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRole().name());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .userId(user.getId())
                .nickname(user.getNickname())
                .build();
    }

    public UserInfoResponse getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        return UserInfoResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .provider(user.getProvider())
                .role(user.getRole())
                .build();
    }

    public void logout(Long userId) {
        jwtTokenProvider.deleteRefreshToken(userId);
    }

    private User findOrCreateUser(OAuthUserInfo userInfo) {
        return userRepository.findByProviderAndSocialId(userInfo.getProvider(), userInfo.getSocialId())
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .socialId(userInfo.getSocialId())
                                .provider(userInfo.getProvider())
                                .email(userInfo.getEmail())
                                .nickname(userInfo.getNickname())
                                .build()
                ));
    }
}
