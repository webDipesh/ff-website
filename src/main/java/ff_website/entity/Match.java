package ff_website.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String matchName;

    @Column(nullable = false)
    private LocalDate matchDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(columnDefinition = "TEXT")
private String prizeRequirement;



    @Column(nullable = false, length = 10)
    private String matchType; // FREE / PAID

    @Column(nullable = false, length = 20)
    private String gameMode; // SOLO / DUO / SQUAD

    @Column(nullable = false, length = 50)
    private String map;

    @Column(nullable = false)
    private Integer totalSlots;

    @Column(nullable = false)
    private Integer entryCoins = 0;

    @Column(nullable = false)
    private Integer prizeCoins = 0;

    @Column(length = 100)
    private String roomId;

    @Column(length = 100)
    private String roomPassword;

    @Column(nullable = false, length = 20)
    private String status = "DRAFT";

    @Column(length = 3000)
    private String matchRules;


    // =========================
    // GETTERS & SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    public LocalDate getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDate matchDate) {
        this.matchDate = matchDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

       public String getPrizeRequirement() {
    return prizeRequirement;
}
public void setPrizeRequirement(String prizeRequirement) {
    this.prizeRequirement = prizeRequirement;
}

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public Integer getTotalSlots() {
        return totalSlots;
    }

    public void setTotalSlots(Integer totalSlots) {
        this.totalSlots = totalSlots;
    }

    public Integer getEntryCoins() {
        return entryCoins;
    }

    public void setEntryCoins(Integer entryCoins) {
        this.entryCoins = entryCoins;
    }

    public Integer getPrizeCoins() {
        return prizeCoins;
    }

    public void setPrizeCoins(Integer prizeCoins) {
        this.prizeCoins = prizeCoins;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomPassword() {
        return roomPassword;
    }

    public void setRoomPassword(String roomPassword) {
        this.roomPassword = roomPassword;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMatchRules() {
        return matchRules;
    }

    public void setMatchRules(String matchRules) {
        this.matchRules = matchRules;
    }
}
