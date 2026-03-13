package OtterStudio.backend_api.domain.game.repository;

import OtterStudio.backend_api.domain.game.entity.Game;
import OtterStudio.backend_api.domain.game.entity.GameStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Integer> {

    Page<Game> findByStatus(GameStatus status, Pageable pageable);

    Page<Game> findByCategoryAndStatus(String category, GameStatus status, Pageable pageable);

    Page<Game> findByCreatorId(Integer creatorId, Pageable pageable);
}
