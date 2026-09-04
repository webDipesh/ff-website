
package ff_website.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ff_website.entity.User;
import ff_website.repository.UserRepository;
import ff_website.service.UserService;

@Controller
public class ProfileController {

    private final UserService userService;
    private final UserRepository userRepository;

    public ProfileController(
            UserService userService,
            UserRepository userRepository) {

        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public String profile(
            Authentication authentication,
            Model model) {

        String email = authentication.getName();

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        model.addAttribute("user", user);

        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            Authentication authentication,
            @RequestParam String ffUid,
            @RequestParam String ffUsername,
            RedirectAttributes redirectAttributes) {

        String email = authentication.getName();

        userService.updateFfProfile(
                email,
                ffUid,
                ffUsername
        );

        redirectAttributes.addFlashAttribute(
                "success",
                "Profile updated successfully!"
        );

        return "redirect:/profile";
    }
}

