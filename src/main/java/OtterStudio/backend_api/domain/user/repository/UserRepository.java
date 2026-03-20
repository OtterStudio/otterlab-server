package OtterStudio.backend_api.domain.user.repository;

import OtterStudio.backend_api.domain.user.entity.SocialProvider;
import OtterStudio.backend_api.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findBySocialId(String socialId);

    Optional<User> findByProviderAndSocialId(SocialProvider provider, String socialId);

    boolean existsByNickname(String nickname);
}
