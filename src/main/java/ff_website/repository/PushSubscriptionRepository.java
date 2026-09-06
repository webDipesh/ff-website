package ff_website.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ff_website.entity.PushSubscription;
import ff_website.entity.User;

public interface PushSubscriptionRepository
        extends JpaRepository<PushSubscription, Long> {

    List<PushSubscription> findByUser(User user);

    Optional<PushSubscription> findByEndpoint(String endpoint);

    void deleteByEndpoint(String endpoint);
}