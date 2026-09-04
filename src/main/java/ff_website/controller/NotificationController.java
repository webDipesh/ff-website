package ff_website.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ff_website.entity.Notification;
import ff_website.entity.User;
import ff_website.repository.UserRepository;
import ff_website.service.NotificationService;

@Controller
public class NotificationController {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public NotificationController(
            UserRepository userRepository,
            NotificationService notificationService) {

        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications")
    public String notifications(
            Authentication authentication,
            Model model) {

        User user = userRepository
                .findByEmailIgnoreCase(
                        authentication.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"));

        List<Notification> notifications =
                notificationService
                        .getUserNotifications(user);

        model.addAttribute(
                "notifications",
                notifications);

        return "notifications";
    }

    @PostMapping("/notifications/read")
    public String markRead(
            @RequestParam Long id) {

        notificationService.markAsRead(id);

        return "redirect:/notifications";
    }
}