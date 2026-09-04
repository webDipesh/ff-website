package ff_website.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "match_results",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_match_result_match",
            columnNames = "match_id"
        )
    }
)
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "match_id",
        nullable = false,
        unique = true
    )
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "winner_user_id",
        nullable = false
    )
    private User winner;

    @Column(nullable = false)
    private Integer winnerSlot;

    @Column(nullable = false)
    private LocalDateTime declaredAt;

    @PrePersist
    protected void onCreate() {
        declaredAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

    public Integer getWinnerSlot() {
        return winnerSlot;
    }

    public void setWinnerSlot(Integer winnerSlot) {
        this.winnerSlot = winnerSlot;
    }

    public LocalDateTime getDeclaredAt() {
        return declaredAt;
    }
}
