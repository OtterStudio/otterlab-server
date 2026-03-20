package OtterStudio.backend_api.global.security.oauth;

import OtterStudio.backend_api.domain.user.entity.SocialProvider;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuthUserInfo {

    private final String socialId;
    private final String email;
    private final String nickname;
    private final SocialProvider provider;
}
