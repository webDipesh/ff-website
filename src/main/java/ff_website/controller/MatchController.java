package ff_website.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import ff_website.entity.Match;
import ff_website.entity.User;
import ff_website.repository.MatchRegistrationRepository;
import ff_website.repository.UserRepository;
import ff_website.service.MatchService;

@Controller
public class MatchController {

    private static final ZoneId NEPAL_ZONE =
            ZoneId.of("Asia/Kathmandu");

    private final MatchService matchService;
    private final MatchRegistrationRepository registrationRepository;
    private final UserRepository userRepository;

    public MatchController(
            MatchService matchService,
            MatchRegistrationRepository registrationRepository,
            UserRepository userRepository) {

        this.matchService = matchService;
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/free-matches")
    public String freeMatches(Model model) {

        List<Match> matches =
                matchService.getPublishedMatchesByType("FREE");

        model.addAttribute("matches", matches);

        return "free-matches";
    }

    @GetMapping("/paid-matches")
    public String paidMatches(Model model) {

        List<Match> matches =
                matchService.getPublishedMatchesByType("PAID");

        model.addAttribute("matches", matches);

        return "paid-matches";
    }

    @GetMapping("/matches/{id}")
    public String matchDetails(
            @PathVariable Long id,
            Model model,
            Authentication authentication) {

        Match match = matchService.getMatchById(id);

        model.addAttribute("match", match);

        User user = userRepository
                .findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"));

        boolean registered =
                registrationRepository
                        .existsByMatchAndUser(match, user);

        model.addAttribute("registered", registered);

        // Nepal current time
        LocalDateTime now =
                LocalDateTime.now(NEPAL_ZONE);

        // Match start time
        LocalDateTime matchStart =
                LocalDateTime.of(
                        match.getMatchDate(),
                        match.getStartTime()
                );

        // Room ID/password release 4 minutes before match
        LocalDateTime roomReleaseTime =
                matchStart.minusMinutes(4);

        /*
         * ONLY TWO CONDITIONS:
         *
         * 1. User registered
         * 2. 4 minutes before match has been reached
         *
         * No slot-full condition.
         */
        boolean roomVisible =
                registered
                && !now.isBefore(roomReleaseTime)
                && now.isBefore(matchStart);

        model.addAttribute(
                "roomVisible",
                roomVisible
        );

        return "match-details";
    }
}