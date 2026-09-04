package ff_website.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ff_website.entity.Match;
import ff_website.entity.MatchRegistration;
import ff_website.entity.User;
import ff_website.repository.MatchRegistrationRepository;
import ff_website.repository.MatchRepository;
import ff_website.repository.UserRepository;

@Service
public class MatchRegistrationService {

    private static final ZoneId NEPAL_ZONE =
            ZoneId.of("Asia/Kathmandu");

    private final MatchRepository matchRepository;
    private final MatchRegistrationRepository registrationRepository;
    private final UserRepository userRepository;

    public MatchRegistrationService(
            MatchRepository matchRepository,
            MatchRegistrationRepository registrationRepository,
            UserRepository userRepository) {

        this.matchRepository = matchRepository;
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // REGISTER USER FOR MATCH
    // =========================================================

    @Transactional
    public MatchRegistration registerUser(
            Long matchId,
            String email) {

        // -----------------------------------------------------
        // 1. Find match
        // -----------------------------------------------------

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Match not found"));

        // -----------------------------------------------------
        // 2. Find user
        // -----------------------------------------------------

        User user = userRepository
                .findByEmailIgnoreCase(email.trim())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"));

        // -----------------------------------------------------
        // 3. Check Free Fire UID and Username
        // -----------------------------------------------------

        if (user.getFfUid() == null
                || user.getFfUid().isBlank()
                || user.getFfUsername() == null
                || user.getFfUsername().isBlank()) {

            throw new IllegalArgumentException(
                    "Please update your Free Fire UID and Username first.");
        }

        // -----------------------------------------------------
        // 4. Match must be PUBLISHED
        // -----------------------------------------------------

        if (!"PUBLISHED".equalsIgnoreCase(
                match.getStatus())) {

            throw new IllegalArgumentException(
                    "This match is not available for registration.");
        }

        // -----------------------------------------------------
        // 5. Current Nepal time
        // -----------------------------------------------------

        LocalDateTime matchStart =
                LocalDateTime.of(
                        match.getMatchDate(),
                        match.getStartTime());

        LocalDateTime now =
                LocalDateTime.now(NEPAL_ZONE);

        // -----------------------------------------------------
        // 6. Registration closes 20 minutes before match
        // -----------------------------------------------------

        LocalDateTime registrationCloseTime =
                matchStart.minusMinutes(20);

        if (!now.isBefore(registrationCloseTime)) {

            throw new IllegalArgumentException(
                    "Registration Time Finished");
        }

        // -----------------------------------------------------
        // 7. Check duplicate registration
        // -----------------------------------------------------

        if (registrationRepository
                .existsByMatchAndUser(match, user)) {

            throw new IllegalArgumentException(
                    "You are already registered for this match.");
        }

        // -----------------------------------------------------
        // 8. Check available slots
        // -----------------------------------------------------

        long registeredCount =
                registrationRepository.countByMatch(match);

        if (registeredCount >= match.getTotalSlots()) {

            throw new IllegalArgumentException(
                    "All slots are already full.");
        }

        // -----------------------------------------------------
        // 9. Get entry fee
        // -----------------------------------------------------

        int entryFee = 0;

        if ("PAID".equalsIgnoreCase(
                match.getMatchType())) {

            entryFee = match.getEntryCoins() == null
                    ? 0
                    : match.getEntryCoins();

            // -------------------------------------------------
            // Paid match must have valid entry fee
            // -------------------------------------------------

            if (entryFee <= 0) {

                throw new IllegalArgumentException(
                        "Invalid entry fee");
            }

            // -------------------------------------------------
            // Check user's coins
            // -------------------------------------------------

            if (user.getCoins() == null
                    || user.getCoins() < entryFee) {

                throw new IllegalArgumentException(
                        "Insufficient Coins");
            }

            // -------------------------------------------------
            // Deduct coins
            // -------------------------------------------------

            user.setCoins(
                    user.getCoins() - entryFee);

            userRepository.saveAndFlush(user);
        }

        // -----------------------------------------------------
        // 10. Assign next slot number
        // -----------------------------------------------------

        int slotNumber =
                (int) registeredCount + 1;

        // -----------------------------------------------------
        // 11. Create registration
        // -----------------------------------------------------

        MatchRegistration registration =
                new MatchRegistration();

        registration.setMatch(match);
        registration.setUser(user);
        registration.setSlotNumber(slotNumber);

        // -----------------------------------------------------
        // 12. Save registration
        // -----------------------------------------------------

        return registrationRepository.save(registration);
    }

    // =========================================================
    // GET REGISTRATIONS FOR A MATCH
    // =========================================================

    public List<MatchRegistration> getRegistrationsForMatch(
            Long matchId) {

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Match not found"));

        return registrationRepository
                .findByMatchOrderBySlotNumberAsc(match);
    }
}