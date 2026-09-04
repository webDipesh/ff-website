package ff_website.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ff_website.entity.Notification;
import ff_website.entity.User;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(
            User user);

    long countByUserAndReadStatusFalse(
            User user);

    boolean existsByUserAndMatchId(
            User user,
            Long matchId);
}