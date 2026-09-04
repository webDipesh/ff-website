package ff_website.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ff_website.entity.User;
import ff_website.entity.WalletSettings;
import ff_website.entity.WithdrawalRequest;
import ff_website.repository.UserRepository;
import ff_website.repository.WalletSettingsRepository;
import ff_website.repository.WithdrawalRequestRepository;

@Service
public class WithdrawalService {

    private final UserRepository userRepository;
    private final WalletSettingsRepository walletSettingsRepository;
    private final WithdrawalRequestRepository withdrawalRequestRepository;
    private final NotificationService notificationService;

    public WithdrawalService(
            UserRepository userRepository,
            WalletSettingsRepository walletSettingsRepository,
            WithdrawalRequestRepository withdrawalRequestRepository,
            NotificationService notificationService) {

        this.userRepository = userRepository;
        this.walletSettingsRepository = walletSettingsRepository;
        this.withdrawalRequestRepository = withdrawalRequestRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public WithdrawalRequest createWithdrawal(
            String email,
            Integer coins,
            String method,
            String walletIdentifier) {

        User user = userRepository
                .findByEmailIgnoreCase(email.trim())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"));

        if (coins == null || coins <= 0) {
            throw new IllegalArgumentException(
                    "Withdrawal coins valid hunuparcha");
        }

        if (method == null ||
                (!method.equalsIgnoreCase("ESEWA")
                        && !method.equalsIgnoreCase("KHALTI"))) {

            throw new IllegalArgumentException(
                    "Withdrawal method eSewa or Khalti hunuparcha");
        }

        if (walletIdentifier == null ||
                walletIdentifier.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Wallet identifier required cha");
        }

        WalletSettings settings;

        if (walletSettingsRepository.count() == 0) {

            settings = new WalletSettings();

            settings.setCoinsPerRupee(10);
            settings.setMinimumWithdrawalCoins(100);

            walletSettingsRepository.save(settings);

        } else {

            settings = walletSettingsRepository
                    .findAll()
                    .get(0);
        }

        if (coins < settings.getMinimumWithdrawalCoins()) {

            throw new IllegalArgumentException(
                    "Minimum withdrawal "
                    + settings.getMinimumWithdrawalCoins()
                    + " coins ho");
        }

        int currentCoins =
                user.getCoins() == null
                        ? 0
                        : user.getCoins();

        if (currentCoins < coins) {

            throw new IllegalArgumentException(
                    "Insufficient coins. Your balance: "
                    + currentCoins);
        }

        BigDecimal amount = BigDecimal.valueOf(coins)
                .divide(
                        BigDecimal.valueOf(
                                settings.getCoinsPerRupee()),
                        2,
                        RoundingMode.HALF_UP
                );

        // Coins deduct
        user.setCoins(currentCoins - coins);

        userRepository.save(user);

        // Create withdrawal request
        WithdrawalRequest request =
                new WithdrawalRequest();

        request.setUser(user);
        request.setCoins(coins);
        request.setAmount(amount);
        request.setMethod(method.toUpperCase());
        request.setWalletIdentifier(
                walletIdentifier.trim());
        request.setStatus("PENDING");

        return withdrawalRequestRepository.save(request);
    }

    @Transactional
    public void approveWithdrawal(
            Long withdrawalId,
            String adminNote) {

        WithdrawalRequest request =
                withdrawalRequestRepository
                        .findById(withdrawalId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Withdrawal request not found"));

        if (!"PENDING".equals(request.getStatus())) {

            throw new IllegalArgumentException(
                    "This withdrawal is already processed");
        }

        request.setStatus("APPROVED");

        request.setCompletedAt(
                LocalDateTime.now());

        if (adminNote != null &&
                !adminNote.trim().isEmpty()) {

            request.setAdminNote(
                    adminNote.trim());
        }

        withdrawalRequestRepository.save(request);

        // User notification
        notificationService.createNotification(
                request.getUser(),
                "Your withdrawal request of Rs. "
                        + request.getAmount()
                        + " has been approved. "
                        + "Your payment will be processed soon."
        );
    }

    @Transactional
    public void rejectWithdrawal(
            Long withdrawalId,
            String adminNote) {

        WithdrawalRequest request =
                withdrawalRequestRepository
                        .findById(withdrawalId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Withdrawal request not found"));

        if (!"PENDING".equals(request.getStatus())) {

            throw new IllegalArgumentException(
                    "This withdrawal is already processed");
        }

        User user = request.getUser();

        int currentCoins =
                user.getCoins() == null
                        ? 0
                        : user.getCoins();

        // Refund coins
        user.setCoins(
                currentCoins + request.getCoins());

        userRepository.save(user);

        request.setStatus("REJECTED");

        request.setCompletedAt(
                LocalDateTime.now());

        if (adminNote != null &&
                !adminNote.trim().isEmpty()) {

            request.setAdminNote(
                    adminNote.trim());
        }

        withdrawalRequestRepository.save(request);

        // User notification
        notificationService.createNotification(
                request.getUser(),
                "Your withdrawal request of Rs. "
                        + request.getAmount()
                        + " was rejected. "
                        + request.getCoins()
                        + " coins have been refunded to your wallet."
        );
    }
}