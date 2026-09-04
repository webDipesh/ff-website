package ff_website.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ff_website.entity.Match;

public interface MatchRepository
        extends JpaRepository<Match, Long> {

    List<Match> findByStatusAndMatchTypeOrderByMatchDateAscStartTimeAsc(
            String status,
            String matchType);
}