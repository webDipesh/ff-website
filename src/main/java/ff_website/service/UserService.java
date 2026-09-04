
package ff_website.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ff_website.entity.User;
import ff_website.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String email, String password) {

        String normalizedEmail = email.trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();

        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(password));
        user.setProvider("LOCAL");
        user.setRole("USER");

        User savedUser = userRepository.save(user);

        System.out.println("===== USER REGISTERED =====");
        System.out.println("ID: " + savedUser.getId());
        System.out.println("EMAIL: " + savedUser.getEmail());
        System.out.println("PROVIDER: " + savedUser.getProvider());
        System.out.println("ROLE: " + savedUser.getRole());

        return savedUser;
    }

    // Update Free Fire profile information
    public User updateFfProfile(
            String email,
            String ffUid,
            String ffUsername) {

        User user = userRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        user.setFfUid(ffUid.trim());
        user.setFfUsername(ffUsername.trim());

        User updatedUser = userRepository.save(user);

        System.out.println("===== FF PROFILE UPDATED =====");
        System.out.println("EMAIL: " + updatedUser.getEmail());
        System.out.println("FF UID: " + updatedUser.getFfUid());
        System.out.println("FF USERNAME: " + updatedUser.getFfUsername());

        return updatedUser;
    }
}

