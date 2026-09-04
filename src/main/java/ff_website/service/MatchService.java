package ff_website.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ff_website.entity.Match;
import ff_website.repository.MatchRegistrationRepository;
import ff_website.repository.MatchRepository;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final MatchRegistrationRepository registrationRepository;

    public MatchService(
            MatchRepository matchRepository,
            MatchRegistrationRepository registrationRepository) {

        this.matchRepository = matchRepository;
        this.registrationRepository = registrationRepository;
    }

    /*
     * SAVE NEW MATCH AS DRAFT
     */
    public Match saveDraft(Match match) {

        validateCommonFields(match);

        if ("FREE".equalsIgnoreCase(
                match.getMatchType())) {

            match.setEntryCoins(0);
        }

        match.setStatus("DRAFT");

        return matchRepository.save(match);
    }


    /*
     * PUBLISH MATCH
     */
    public Match publishMatch(Long id) {

        Match match =
                getMatchById(id);

        validateCommonFields(match);

        if ("FREE".equalsIgnoreCase(
                match.getMatchType())) {

            match.setEntryCoins(0);
        }

        if ("PAID".equalsIgnoreCase(
                match.getMatchType())
                && (match.getEntryCoins() == null
                || match.getEntryCoins() <= 0)) {

            throw new IllegalArgumentException(
                    "Paid match ko entry coins 0 bhanda greater hunuparcha."
            );
        }

        match.setStatus("PUBLISHED");

        return matchRepository.save(match);
    }


    /*
     * UPDATE EXISTING MATCH
     */
    public Match updateMatch(
            Long id,
            Match updatedMatch) {

        Match existingMatch =
                matchRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Match not found"
                                ));

        validateCommonFields(updatedMatch);

        if ("FREE".equalsIgnoreCase(
                updatedMatch.getMatchType())) {

            updatedMatch.setEntryCoins(0);
        }

        if ("PAID".equalsIgnoreCase(
                updatedMatch.getMatchType())
                && (updatedMatch.getEntryCoins() == null
                || updatedMatch.getEntryCoins() <= 0)) {

            throw new IllegalArgumentException(
                    "Paid match ko entry coins 0 bhanda greater hunuparcha."
            );
        }


        existingMatch.setMatchName(
                updatedMatch.getMatchName());

        existingMatch.setMatchDate(
                updatedMatch.getMatchDate());

        existingMatch.setStartTime(
                updatedMatch.getStartTime());

        existingMatch.setMatchType(
                updatedMatch.getMatchType());

        existingMatch.setGameMode(
                updatedMatch.getGameMode());

        existingMatch.setMap(
                updatedMatch.getMap());

        existingMatch.setTotalSlots(
                updatedMatch.getTotalSlots());

        existingMatch.setEntryCoins(
                updatedMatch.getEntryCoins());

        existingMatch.setPrizeCoins(
                updatedMatch.getPrizeCoins());

        existingMatch.setRoomId(
                updatedMatch.getRoomId());

        existingMatch.setRoomPassword(
                updatedMatch.getRoomPassword());

        existingMatch.setPrizeRequirement(
                updatedMatch.getPrizeRequirement());

        existingMatch.setMatchRules(
                updatedMatch.getMatchRules());


        /*
         * IMPORTANT:
         *
         * Existing match ko status change
         * gareko chaina.
         *
         * DRAFT → DRAFT
         * PUBLISHED → PUBLISHED
         */
        return matchRepository.save(existingMatch);
    }


    /*
     * DELETE MATCH
     *
     * ADMIN CAN DELETE ANY MATCH
     *
     * No date condition.
     * No status condition.
     * No slot condition.
     *
     * First registrations are deleted,
     * then match is deleted.
     */
    @Transactional
    public void deleteMatch(Long id) {

        Match match =
                matchRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Match not found"
                                ));


        /*
         * First delete registrations
         * belonging to this match.
         */
        registrationRepository.deleteByMatch(match);


        /*
         * Then delete the match itself.
         */
        matchRepository.delete(match);
    }


    /*
     * VALIDATE MATCH FIELDS
     */
    private void validateCommonFields(
            Match match) {

        if (match.getMatchName() == null
                || match.getMatchName().isBlank()) {

            throw new IllegalArgumentException(
                    "Match name required cha."
            );
        }


        if (match.getMatchDate() == null) {

            throw new IllegalArgumentException(
                    "Match date required cha."
            );
        }


        if (match.getStartTime() == null) {

            throw new IllegalArgumentException(
                    "Start time required cha."
            );
        }


        if (!"FREE".equalsIgnoreCase(
                match.getMatchType())
                && !"PAID".equalsIgnoreCase(
                        match.getMatchType())) {

            throw new IllegalArgumentException(
                    "Invalid match type."
            );
        }


        if (!"SOLO".equalsIgnoreCase(
                match.getGameMode())
                && !"DUO".equalsIgnoreCase(
                        match.getGameMode())
                && !"SQUAD".equalsIgnoreCase(
                        match.getGameMode())) {

            throw new IllegalArgumentException(
                    "Invalid game mode."
            );
        }


        if (match.getMap() == null
                || match.getMap().isBlank()) {

            throw new IllegalArgumentException(
                    "Map required cha."
            );
        }


        if (match.getTotalSlots() == null
                || match.getTotalSlots() <= 0) {

            throw new IllegalArgumentException(
                    "Total slots 0 bhanda greater hunuparcha."
            );
        }


        if (match.getEntryCoins() == null
                || match.getEntryCoins() < 0) {

            throw new IllegalArgumentException(
                    "Entry coins invalid cha."
            );
        }


        if (match.getPrizeCoins() == null
                || match.getPrizeCoins() < 0) {

            throw new IllegalArgumentException(
                    "Prize coins invalid cha."
            );
        }


        if (match.getMatchRules() == null
                || match.getMatchRules().isBlank()) {

            throw new IllegalArgumentException(
                    "Match rules required cha."
            );
        }


        if (match.getPrizeRequirement() == null
                || match.getPrizeRequirement().isBlank()) {

            throw new IllegalArgumentException(
                    "Prize requirement required cha."
            );
        }
    }


    /*
     * GET PUBLISHED MATCHES BY TYPE
     */
    public List<Match> getPublishedMatchesByType(
            String matchType) {

        return matchRepository
                .findByStatusAndMatchTypeOrderByMatchDateAscStartTimeAsc(
                        "PUBLISHED",
                        matchType
                );
    }


    /*
     * GET ALL MATCHES
     */
    public List<Match> getAllMatches() {

        return matchRepository.findAll();
    }


    /*
     * GET MATCH BY ID
     */
    public Match getMatchById(Long id) {

        return matchRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Match not found"
                        ));
    }
}