package ff_website.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ff_website.service.MatchRegistrationService;

@Controller
public class MatchRegistrationController {

    private final MatchRegistrationService registrationService;

    public MatchRegistrationController(
            MatchRegistrationService registrationService) {

        this.registrationService = registrationService;
    }


    @PostMapping("/matches/{id}/register")
    public String register(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {

            registrationService.registerUser(
                    id,
                    authentication.getName()
            );

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Registration successful!"
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/matches/" + id;
    }
}