package ff_website.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import ff_website.entity.PushSubscription;
import ff_website.entity.User;
import ff_website.repository.PushSubscriptionRepository;
import ff_website.repository.UserRepository;

@RestController
@RequestMapping("/api/push")
public class PushSubscriptionController {

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final UserRepository userRepository;

    private final String vapidPublicKey;

    public PushSubscriptionController(
            PushSubscriptionRepository pushSubscriptionRepository,
            UserRepository userRepository,
            @Value("${vapid.public-key:}") String vapidPublicKey) {

        this.pushSubscriptionRepository =
                pushSubscriptionRepository;

        this.userRepository =
                userRepository;

        this.vapidPublicKey =
                vapidPublicKey;
    }


    /*
     * Return VAPID public key
     */
    @GetMapping("/public-key")
    public ResponseEntity<String> getPublicKey() {

        if (vapidPublicKey == null
                || vapidPublicKey.isBlank()) {

            return ResponseEntity
                    .internalServerError()
                    .body("VAPID public key is not configured");
        }

        return ResponseEntity.ok(
                vapidPublicKey
        );
    }


    /*
     * Save browser push subscription
     */
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(
            @RequestBody PushSubscriptionRequest request,
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return ResponseEntity
                    .status(401)
                    .build();
        }


        User user =
                userRepository
                        .findByEmailIgnoreCase(
                                authentication.getName()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found"
                                )
                        );


        if (request.endpoint() == null
                || request.endpoint().isBlank()
                || request.keys() == null
                || request.keys().p256dh() == null
                || request.keys().auth() == null) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Invalid push subscription"
                    );
        }


        PushSubscription subscription =
                pushSubscriptionRepository
                        .findByEndpoint(
                                request.endpoint()
                        )
                        .orElseGet(
                                PushSubscription::new
                        );


        subscription.setUser(user);

        subscription.setEndpoint(
                request.endpoint()
        );

        subscription.setP256dh(
                request.keys().p256dh()
        );

        subscription.setAuth(
                request.keys().auth()
        );


        pushSubscriptionRepository.save(
                subscription
        );


        return ResponseEntity
                .ok()
                .body(
                        "Push subscription saved"
                );
    }


    public record PushSubscriptionRequest(
            String endpoint,
            Keys keys) {
    }


    public record Keys(
            String p256dh,
            String auth) {
    }
}