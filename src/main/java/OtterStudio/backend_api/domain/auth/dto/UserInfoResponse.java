package OtterStudio.backend_api.domain.auth.dto;

import OtterStudio.backend_api.domain.user.entity.Role;
import OtterStudio.backend_api.domain.user.entity.SocialProvider;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponse {

    private final Long id;
    private final String email;
    private final String nickname;
    private final SocialProvider provider;
    private final Role role;
}
