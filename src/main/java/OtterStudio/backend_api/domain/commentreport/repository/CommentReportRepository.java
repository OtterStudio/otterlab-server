package OtterStudio.backend_api.domain.commentreport.repository;

import OtterStudio.backend_api.domain.commentreport.entity.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReportRepository extends JpaRepository<CommentReport, Integer> {

    boolean existsByCommentIdAndReporterId(Integer commentId, Integer reporterId);
}
