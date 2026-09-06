package ff_website.service;

import java.security.Security;
import java.util.Base64;
import java.util.List;

import org.apache.http.HttpResponse;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ff_website.entity.PushSubscription;
import ff_website.entity.User;
import ff_website.repository.PushSubscriptionRepository;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Urgency;

@Service
public class PushNotificationService {

    private final PushSubscriptionRepository subscriptionRepository;

    private final String vapidPublicKey;
    private final String vapidPrivateKey;
    private final String vapidSubject;

    public PushNotificationService(
            PushSubscriptionRepository subscriptionRepository,
            @Value("${vapid.public-key:}") String vapidPublicKey,
            @Value("${vapid.private-key:}") String vapidPrivateKey,
            @Value("${vapid.subject:}") String vapidSubject) {

        this.subscriptionRepository = subscriptionRepository;
        this.vapidPublicKey = vapidPublicKey;
        this.vapidPrivateKey = vapidPrivateKey;
        this.vapidSubject = vapidSubject;

        if (Security.getProvider(
                BouncyCastleProvider.PROVIDER_NAME) == null) {

            Security.addProvider(
                    new BouncyCastleProvider());
        }
    }

    public void sendToUser(
            User user,
            String message) {

        if (user == null) {
            return;
        }

        if (vapidPublicKey == null
                || vapidPublicKey.isBlank()
                || vapidPrivateKey == null
                || vapidPrivateKey.isBlank()
                || vapidSubject == null
                || vapidSubject.isBlank()) {

            System.err.println(
                    "Web Push skipped: VAPID keys are not configured."
            );

            return;
        }

        List<PushSubscription> subscriptions =
                subscriptionRepository.findByUser(user);

        if (subscriptions == null
                || subscriptions.isEmpty()) {

            return;
        }

        for (PushSubscription subscription
                : subscriptions) {

            try {

                byte[] publicKeyBytes =
                        Base64.getUrlDecoder()
                                .decode(
                                        subscription.getP256dh()
                                );

                byte[] authBytes =
                        Base64.getUrlDecoder()
                                .decode(
                                        subscription.getAuth()
                                );

                byte[] payload =
                        (
                            "{"
                            + "\"title\":\"FF Website\","
                            + "\"body\":"
                            + quote(message)
                            + ","
                            + "\"url\":\"/notifications\""
                            + "}"
                        ).getBytes(
                                java.nio.charset.StandardCharsets.UTF_8
                        );

                ECPublicKey userPublicKey =
                        (ECPublicKey)
                                nl.martijndwars.webpush.Utils
                                        .loadPublicKey(
                                                publicKeyBytes
                                        );

                Notification notification =
                        new Notification(
                                subscription.getEndpoint(),
                                userPublicKey,
                                authBytes,
                                payload,
                                86400,
                                Urgency.NORMAL,
                                null
                        );

                PushService pushService =
                        new PushService(
                                vapidPublicKey,
                                vapidPrivateKey,
                                vapidSubject
                        );

                HttpResponse response =
                        pushService.send(notification);

                if (response != null) {

                    int statusCode =
                            response.getStatusLine()
                                    .getStatusCode();

                    if (statusCode < 200
                            || statusCode >= 300) {

                        System.err.println(
                                "Push notification returned HTTP "
                                + statusCode
                                + " for subscription "
                                + subscription.getId()
                        );
                    }
                }

            } catch (Exception e) {

                System.err.println(
                        "Push notification failed for subscription "
                        + subscription.getId()
                        + ": "
                        + e.getMessage()
                );
            }
        }
    }

    private String quote(String value) {

        if (value == null) {
            return "\"\"";
        }

        return "\""
                + value
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\n", "\\n")
                        .replace("\r", "\\r")
                + "\"";
    }
}