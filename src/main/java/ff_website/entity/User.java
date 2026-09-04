package ff_website.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_email",
            columnNames = "email"
        )
    }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(length = 255)
    private String password;

    @Column(nullable = false, length = 20)
    private String provider = "LOCAL";

    @Column(nullable = false, length = 20)
    private String role = "USER";

    // Free Fire UID
    @Column(length = 100)
    private String ffUid;

    // Free Fire Username
    @Column(length = 100)
    private String ffUsername;

    // User's total coin balance
    @Column(nullable = false)
    private Integer coins = 0;


    // =========================
    // GETTERS AND SETTERS
    // =========================

    public Long getId() {
        return id;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public String getFfUid() {
        return ffUid;
    }

    public void setFfUid(String ffUid) {
        this.ffUid = ffUid;
    }


    public String getFfUsername() {
        return ffUsername;
    }

    public void setFfUsername(String ffUsername) {
        this.ffUsername = ffUsername;
    }


    public Integer getCoins() {
        return coins;
    }

    public void setCoins(Integer coins) {
        this.coins = coins;
    }
}


