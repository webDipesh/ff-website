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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "match_registrations",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_match_user",
            columnNames = {"match_id", "user_id"}
        ),
        @UniqueConstraint(
            name = "uk_match_slot",
            columnNames = {"match_id", "slot_number"}
        )
    }
)
public class MatchRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "slot_number", nullable = false)
    private Integer slotNumber;

    @Column(nullable = false)
    private LocalDateTime registeredAt;


    @PrePersist
    protected void onCreate() {
        registeredAt = LocalDateTime.now();
    }


    // ID
    public Long getId() {
        return id;
    }


    // Match
    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }


    // User
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    // Slot Number
    public Integer getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(Integer slotNumber) {
        this.slotNumber = slotNumber;
    }


    // Registered At
    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }
}

