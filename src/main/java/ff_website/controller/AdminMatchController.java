package ff_website.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ff_website.entity.Match;
import ff_website.service.MatchRegistrationService;
import ff_website.service.MatchResultService;
import ff_website.service.MatchService;

@Controller
public class AdminMatchController {

    private final MatchService matchService;
    private final MatchRegistrationService registrationService;
    private final MatchResultService matchResultService;

    public AdminMatchController(
            MatchService matchService,
            MatchRegistrationService registrationService,
            MatchResultService matchResultService) {

        this.matchService = matchService;
        this.registrationService = registrationService;
        this.matchResultService = matchResultService;
    }

    /*
     * CREATE NEW MATCH
     */
    @GetMapping("/admin/matches/new")
    public String newMatch(Model model) {

        model.addAttribute(
                "match",
                new Match()
        );

        return "admin-match-form";
    }

    /*
     * SAVE MATCH AS DRAFT
     */
    @PostMapping("/admin/matches")
    public String saveDraft(
            @ModelAttribute Match match,
            RedirectAttributes redirectAttributes) {

        try {

            matchService.saveDraft(match);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Match saved as draft successfully!"
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/admin/matches/new";
    }

    /*
     * MANAGE ALL MATCHES
     */
    @GetMapping("/admin/matches")
    public String manageMatches(Model model) {

        model.addAttribute(
                "matches",
                matchService.getAllMatches()
        );

        return "admin-matches";
    }

    /*
     * EDIT MATCH
     */
    @GetMapping("/admin/matches/{id}/edit")
    public String editMatch(
            @PathVariable Long id,
            Model model) {

        Match match =
                matchService.getMatchById(id);

        model.addAttribute(
                "match",
                match
        );

        return "admin-match-form";
    }

    /*
     * UPDATE MATCH
     */
    @PostMapping("/admin/matches/{id}/update")
    public String updateMatch(
            @PathVariable Long id,
            @ModelAttribute Match match,
            RedirectAttributes redirectAttributes) {

        try {

            matchService.updateMatch(
                    id,
                    match
            );

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Match updated successfully!"
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/admin/matches";
    }

    /*
     * PUBLISH MATCH
     */
    @PostMapping("/admin/matches/{id}/publish")
    public String publishMatch(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            matchService.publishMatch(id);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Match published successfully!"
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/admin/matches";
    }

    /*
     * VIEW MATCH REGISTRATIONS
     */
    @GetMapping("/admin/matches/{id}/registrations")
    public String viewRegistrations(
            @PathVariable Long id,
            Model model) {

        Match match =
                matchService.getMatchById(id);

        model.addAttribute(
                "match",
                match
        );

        model.addAttribute(
                "registrations",
                registrationService
                        .getRegistrationsForMatch(id)
        );

        return "admin-match-registrations";
    }

    /*
     * DECLARE MATCH WINNER
     */
    @PostMapping("/admin/matches/{id}/declare-winner")
    public String declareWinner(
            @PathVariable Long id,
            @RequestParam Long registrationId,
            RedirectAttributes redirectAttributes) {

        try {

            matchResultService.declareWinner(
                    id,
                    registrationId
            );

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Winner declared successfully!"
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/admin/matches/"
                + id
                + "/registrations";
    }

    /*
     * DELETE ANY MATCH
     *
     * Admin can delete:
     * - DRAFT
     * - PUBLISHED
     * - UPCOMING
     * - FINISHED
     */
    @PostMapping("/admin/matches/{id}/delete")
    public String deleteMatch(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            matchService.deleteMatch(id);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Match deleted successfully!"
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/admin/matches";
    }
}