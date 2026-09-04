package ff_website.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ff_website.entity.Match;
import ff_website.entity.MatchResult;

public interface MatchResultRepository
        extends JpaRepository<MatchResult, Long> {

    Optional<MatchResult> findByMatch(Match match);

    boolean existsByMatch(Match match);
}
