package OtterStudio.backend_api.global.security.oauth;

import OtterStudio.backend_api.domain.user.entity.SocialProvider;

public interface OAuthProvider {

    SocialProvider getProvider();

    OAuthUserInfo getUserInfo(String authorizationCode);
}
