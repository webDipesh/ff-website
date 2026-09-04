package ff_website.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ff_website.service.UserService;

@Controller
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String email,
            @RequestParam String password,
            Model model) {

        try {

            System.out.println("===== REGISTER REQUEST =====");
            System.out.println("EMAIL: " + email);

            userService.registerUser(email, password);

            System.out.println("===== REGISTER REDIRECT =====");

            return "redirect:/login?registered";

        } catch (IllegalArgumentException e) {

            System.out.println("REGISTER ERROR: " + e.getMessage());

            model.addAttribute("error", e.getMessage());

            return "register";
        }
    }
}