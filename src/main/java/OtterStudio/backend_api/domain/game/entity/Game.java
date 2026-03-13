package OtterStudio.backend_api.domain.game.entity;

import OtterStudio.backend_api.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "games", indexes = {
        @Index(name = "idx_game_category_status", columnList = "category, status")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    @Column(nullable = false)
    private String title;

    @Column(name = "option_a_text", nullable = false, columnDefinition = "TEXT")
    private String optionAText;

    @Column(name = "option_b_text", nullable = false, columnDefinition = "TEXT")
    private String optionBText;

    @Column(length = 50)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private GameStatus status = GameStatus.PENDING;

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
