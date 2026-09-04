package ff_website.service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ff_website.entity.Match;
import ff_website.entity.MatchRegistration;
import ff_website.entity.MatchResult;
import ff_website.entity.User;
import ff_website.repository.MatchRegistrationRepository;
import ff_website.repository.MatchRepository;
import ff_website.repository.MatchResultRepository;
import ff_website.repository.UserRepository;

@Service
public class MatchResultService {

    private static final ZoneId NEPAL_ZONE =
            ZoneId.of("Asia/Kathmandu");

    private final MatchRepository matchRepository;
    private final MatchRegistrationRepository registrationRepository;
    private final MatchResultRepository resultRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public MatchResultService(
            MatchRepository matchRepository,
            MatchRegistrationRepository registrationRepository,
            MatchResultRepository resultRepository,
            UserRepository userRepository,
            NotificationService notificationService) {

        this.matchRepository = matchRepository;
        this.registrationRepository = registrationRepository;
        this.resultRepository = resultRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public void declareWinner(
            Long matchId,
            Long registrationId) {

        Match match = matchRepository
                .findById(matchId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Match not found"));

        MatchRegistration registration =
                registrationRepository
                        .findById(registrationId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Registration not found"));

        if (!registration.getMatch()
                .getId()
                .equals(match.getId())) {

            throw new IllegalArgumentException(
                    "Invalid registration for this match.");
        }

        // Prevent duplicate winner declaration
        if (resultRepository.existsByMatch(match)) {

            throw new IllegalArgumentException(
                    "Winner has already been declared for this match.");
        }

        LocalDateTime now =
                LocalDateTime.now(NEPAL_ZONE);

        LocalDateTime matchStart =
                LocalDateTime.of(
                        match.getMatchDate(),
                        match.getStartTime());

        if (now.isBefore(matchStart)) {

            throw new IllegalArgumentException(
                    "Winner can be declared after the match starts.");
        }

        // =========================
        // CREATE MATCH RESULT
        // =========================

        MatchResult result = new MatchResult();

        result.setMatch(match);
        result.setWinner(registration.getUser());
        result.setWinnerSlot(registration.getSlotNumber());

        resultRepository.save(result);


        // =========================
        // ADD PRIZE TO WINNER WALLET
        // =========================

        User winner = registration.getUser();

        int currentCoins =
                winner.getCoins() == null
                        ? 0
                        : winner.getCoins();

        int prizeCoins =
                match.getPrizeCoins() == null
                        ? 0
                        : match.getPrizeCoins();

        winner.setCoins(currentCoins + prizeCoins);

        userRepository.save(winner);


        // =========================
        // WINNER NOTIFICATION
        // =========================

        String message =
                "Congratulations! You are the winner of "
                + match.getMatchName()
                + " (Slot "
                + registration.getSlotNumber()
                + "). You received "
                + prizeCoins
                + " coins as prize.";

        notificationService.createNotification(
                winner,
                message,
                match.getId()
        );
    }
}