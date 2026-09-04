package ff_website.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ff_website.entity.WithdrawalRequest;
import ff_website.repository.WithdrawalRequestRepository;
import ff_website.service.WithdrawalService;

@Controller
public class AdminWithdrawalController {

    private final WithdrawalRequestRepository withdrawalRequestRepository;
    private final WithdrawalService withdrawalService;

    public AdminWithdrawalController(
            WithdrawalRequestRepository withdrawalRequestRepository,
            WithdrawalService withdrawalService) {

        this.withdrawalRequestRepository = withdrawalRequestRepository;
        this.withdrawalService = withdrawalService;
    }

    @GetMapping("/admin/withdrawals")
    public String withdrawals(Model model) {

        List<WithdrawalRequest> pendingWithdrawals =
                withdrawalRequestRepository
                        .findByStatusOrderByRequestedAtAsc("PENDING");

        model.addAttribute("withdrawals", pendingWithdrawals);

        return "admin-withdrawals";
    }

    @PostMapping("/admin/withdrawals/approve")
    public String approve(
            @RequestParam Long id,
            @RequestParam(required = false) String adminNote,
            RedirectAttributes redirectAttributes) {

        try {

            withdrawalService.approveWithdrawal(id, adminNote);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Withdrawal approved successfully!"
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/admin/withdrawals";
    }

    @PostMapping("/admin/withdrawals/reject")
    public String reject(
            @RequestParam Long id,
            @RequestParam(required = false) String adminNote,
            RedirectAttributes redirectAttributes) {

        try {

            withdrawalService.rejectWithdrawal(id, adminNote);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Withdrawal rejected and coins refunded!"
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/admin/withdrawals";
    }
}