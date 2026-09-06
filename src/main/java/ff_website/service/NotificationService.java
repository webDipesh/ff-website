package ff_website.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ff_website.entity.Notification;
import ff_website.entity.User;
import ff_website.repository.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final PushNotificationService pushNotificationService;

    public NotificationService(
            NotificationRepository notificationRepository,
            PushNotificationService pushNotificationService) {

        this.notificationRepository = notificationRepository;
        this.pushNotificationService = pushNotificationService;
    }

    public void createNotification(
            User user,
            String message) {

        createNotification(user, message, null);
    }

    public void createNotification(
            User user,
            String message,
            Long matchId) {

        // =========================
        // SAVE WEBSITE NOTIFICATION
        // =========================

        Notification notification =
                new Notification();

        notification.setUser(user);
        notification.setMessage(message);
        notification.setMatchId(matchId);
        notification.setReadStatus(false);

        notificationRepository.save(notification);


        // =========================
        // SEND MOBILE PUSH
        // =========================

        try {

            pushNotificationService
                    .sendToUser(user, message);

        } catch (Exception e) {

            // Push fail भए पनि website notification
            // भने successfully save भइसकेको हुन्छ।

            System.err.println(
                    "Mobile push notification failed: "
                    + e.getMessage()
            );
        }
    }

    public List<Notification> getUserNotifications(
            User user) {

        return notificationRepository
                .findByUserOrderByCreatedAtDesc(user);
    }

    public long getUnreadCount(User user) {

        return notificationRepository
                .countByUserAndReadStatusFalse(user);
    }

    public void markAsRead(Long notificationId) {

        Notification notification =
                notificationRepository
                        .findById(notificationId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Notification not found"));

        notification.setReadStatus(true);

        notificationRepository.save(notification);
    }
}