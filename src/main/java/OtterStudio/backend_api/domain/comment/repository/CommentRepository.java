package OtterStudio.backend_api.domain.comment.repository;

import OtterStudio.backend_api.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    Page<Comment> findByGameIdOrderByCreatedAtDesc(Integer gameId, Pageable pageable);

    long countByGameId(Integer gameId);
}
