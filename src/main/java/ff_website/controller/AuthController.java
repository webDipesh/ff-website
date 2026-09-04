package ff_website.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import ff_website.entity.User;
import ff_website.repository.UserRepository;
import ff_website.service.NotificationService;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public AuthController(
            UserRepository userRepository,
            NotificationService notificationService) {

        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(
            Authentication authentication,
            Model model) {

        String email = authentication.getName();

        User user = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"));

        long notificationCount =
                notificationService.getUnreadCount(user);

        model.addAttribute("user", user);

        model.addAttribute(
                "notificationCount",
                notificationCount);

        return "dashboard";
    }
}