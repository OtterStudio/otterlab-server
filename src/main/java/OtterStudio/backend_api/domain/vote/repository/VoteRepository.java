package OtterStudio.backend_api.domain.vote.repository;

import OtterStudio.backend_api.domain.vote.entity.Vote;
import OtterStudio.backend_api.global.entity.SelectedOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Integer> {

    Optional<Vote> findByUserIdAndGameId(Integer userId, Integer gameId);

    boolean existsByUserIdAndGameId(Integer userId, Integer gameId);

    long countByGameIdAndSelectedOption(Integer gameId, SelectedOption selectedOption);
}
