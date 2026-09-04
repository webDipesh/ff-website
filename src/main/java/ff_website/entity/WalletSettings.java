package ff_website.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "wallet_settings")
public class WalletSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Example: 10 means 10 coins = Rs. 1
    @Column(nullable = false)
    private Integer coinsPerRupee = 10;

    // Minimum coins required for withdrawal
    @Column(nullable = false)
    private Integer minimumWithdrawalCoins = 100;


    public Long getId() {
        return id;
    }


    public Integer getCoinsPerRupee() {
        return coinsPerRupee;
    }

    public void setCoinsPerRupee(Integer coinsPerRupee) {
        this.coinsPerRupee = coinsPerRupee;
    }


    public Integer getMinimumWithdrawalCoins() {
        return minimumWithdrawalCoins;
    }

    public void setMinimumWithdrawalCoins(Integer minimumWithdrawalCoins) {
        this.minimumWithdrawalCoins = minimumWithdrawalCoins;
    }
}