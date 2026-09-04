package ff_website.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ff_website.entity.User;
import ff_website.entity.WalletSettings;
import ff_website.entity.WithdrawalRequest;
import ff_website.repository.UserRepository;
import ff_website.repository.WalletSettingsRepository;
import ff_website.service.WithdrawalService;

@Controller
public class WalletController {

    private final UserRepository userRepository;
    private final WalletSettingsRepository walletSettingsRepository;
    private final WithdrawalService withdrawalService;

    public WalletController(
            UserRepository userRepository,
            WalletSettingsRepository walletSettingsRepository,
            WithdrawalService withdrawalService) {

        this.userRepository = userRepository;
        this.walletSettingsRepository = walletSettingsRepository;
        this.withdrawalService = withdrawalService;
    }

    @GetMapping("/wallet")
    public String wallet(
            org.springframework.security.core.Authentication authentication,
            Model model) {

        String email = authentication.getName();

        User user = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        // Get wallet settings
        WalletSettings settings;

        if (walletSettingsRepository.count() == 0) {

            settings = new WalletSettings();

            // 10 coins = Rs. 1
            // Therefore 100 coins = Rs. 10
            settings.setCoinsPerRupee(10);

            // Minimum withdrawal = 100 coins
            settings.setMinimumWithdrawalCoins(100);

            walletSettingsRepository.save(settings);

        } else {

            settings = walletSettingsRepository.findAll()
                    .get(0);
        }

        int coins = user.getCoins() == null
                ? 0
                : user.getCoins();

        int coinsPerRupee = settings.getCoinsPerRupee();

        double rupeeValue =
                (double) coins / coinsPerRupee;

        model.addAttribute("user", user);
        model.addAttribute("coins", coins);
        model.addAttribute("coinsPerRupee", coinsPerRupee);
        model.addAttribute("rupeeValue", rupeeValue);

        model.addAttribute(
                "minimumWithdrawalCoins",
                settings.getMinimumWithdrawalCoins()
        );

        return "wallet";
    }


    // Withdrawal
    @PostMapping("/wallet/withdraw")
    public String withdraw(
            org.springframework.security.core.Authentication authentication,
            @RequestParam Integer coins,
            @RequestParam String method,
            @RequestParam String walletIdentifier,
            RedirectAttributes redirectAttributes) {

        try {

            WithdrawalRequest request =
                    withdrawalService.createWithdrawal(
                            authentication.getName(),
                            coins,
                            method,
                            walletIdentifier
                    );

            redirectAttributes.addFlashAttribute(
                    "withdrawSuccess",
                    "Withdrawal request submitted successfully! " +
                    "Rs. " + request.getAmount() +
                    " will be paid to your " +
                    request.getMethod() +
                    " wallet within 12 hours."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "withdrawError",
                    e.getMessage()
            );
        }

        return "redirect:/wallet";
    }
}