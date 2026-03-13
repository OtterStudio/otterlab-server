package OtterStudio.backend_api.domain.compatibility.entity;

import OtterStudio.backend_api.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "compatibility_tests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class CompatibilityTest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    private String title;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "question_ids", nullable = false, columnDefinition = "integer[]")
    private Integer[] questionIds;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "creator_answers", nullable = false, columnDefinition = "char(1)[]")
    private String[] creatorAnswers;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
