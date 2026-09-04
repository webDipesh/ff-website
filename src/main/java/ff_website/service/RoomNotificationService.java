package ff_website.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ff_website.entity.Match;
import ff_website.entity.MatchRegistration;
import ff_website.repository.MatchRegistrationRepository;
import ff_website.repository.MatchRepository;
import ff_website.repository.NotificationRepository;

@Service
public class RoomNotificationService {

    private static final ZoneId NEPAL_ZONE =
            ZoneId.of("Asia/Kathmandu");

    private final MatchRepository matchRepository;
    private final MatchRegistrationRepository registrationRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    public RoomNotificationService(
            MatchRepository matchRepository,
            MatchRegistrationRepository registrationRepository,
            NotificationRepository notificationRepository,
            NotificationService notificationService) {

        this.matchRepository = matchRepository;
        this.registrationRepository = registrationRepository;
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
    }

    @Scheduled(fixedRate = 30000)
    public void sendRoomNotifications() {

        // Nepal current time
        LocalDateTime now =
                LocalDateTime.now(NEPAL_ZONE);

        List<Match> matches =
                matchRepository.findAll();

        for (Match match : matches) {

            if (!"PUBLISHED".equalsIgnoreCase(
                    match.getStatus())) {
                continue;
            }

            if (match.getMatchDate() == null
                    || match.getStartTime() == null) {
                continue;
            }

            if (match.getRoomId() == null
                    || match.getRoomId().isBlank()
                    || match.getRoomPassword() == null
                    || match.getRoomPassword().isBlank()) {
                continue;
            }

            LocalDateTime matchStart =
                    LocalDateTime.of(
                            match.getMatchDate(),
                            match.getStartTime()
                    );

            LocalDateTime releaseTime =
                    matchStart.minusMinutes(4);

            // 4 minutes भन्दा अगाडि notification नजाने
            if (now.isBefore(releaseTime)) {
                continue;
            }

            // Match सुरु भएपछि notification नजाने
            if (!now.isBefore(matchStart)) {
                continue;
            }

            List<MatchRegistration> registrations =
                    registrationRepository
                            .findByMatchOrderBySlotNumberAsc(
                                    match
                            );

            for (MatchRegistration registration
                    : registrations) {

                // Duplicate notification रोक्ने
                if (notificationRepository
                        .existsByUserAndMatchId(
                                registration.getUser(),
                                match.getId())) {
                    continue;
                }

                notificationService.createNotification(
                        registration.getUser(),
                        "Room details are now available for "
                                + match.getMatchName()
                                + ".",
                        match.getId()
                );
            }
        }
    }
}