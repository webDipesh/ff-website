package ff_website.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ff_website.entity.Notification;
import ff_website.entity.User;
import ff_website.repository.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(
            NotificationRepository notificationRepository) {

        this.notificationRepository = notificationRepository;
    }

    // Existing notifications without match ID
    public void createNotification(
            User user,
            String message) {

        createNotification(user, message, null);
    }

    // Notification with match ID
    public void createNotification(
            User user,
            String message,
            Long matchId) {

        Notification notification =
                new Notification();

        notification.setUser(user);
        notification.setMessage(message);
        notification.setMatchId(matchId);
        notification.setReadStatus(false);

        notificationRepository.save(notification);
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
