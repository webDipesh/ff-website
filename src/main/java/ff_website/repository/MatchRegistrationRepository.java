package ff_website.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ff_website.entity.Match;
import ff_website.entity.MatchRegistration;
import ff_website.entity.User;

public interface MatchRegistrationRepository
        extends JpaRepository<MatchRegistration, Long> {

    long countByMatch(Match match);

    boolean existsByMatchAndUser(
            Match match,
            User user);

    Optional<MatchRegistration> findByMatchAndUser(
            Match match,
            User user);

    List<MatchRegistration> findByMatchOrderBySlotNumberAsc(
            Match match);

    /*
     * Deletes all registrations belonging
     * to the specified match.
     */
    void deleteByMatch(Match match);
}