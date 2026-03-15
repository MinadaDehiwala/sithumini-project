package com.aurabloom.service;

import com.aurabloom.dto.MeditationSessionRequest;
import com.aurabloom.dto.MeditationSessionResponse;
import com.aurabloom.dto.MeditationStatsResponse;
import com.aurabloom.entity.MeditationSession;
import com.aurabloom.entity.UserAccount;
import com.aurabloom.repository.MeditationSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MeditationService {

    private final MeditationSessionRepository meditationSessionRepository;
    private final UserService userService;
    private final GamificationService gamificationService;

    public MeditationSessionResponse create(String email, MeditationSessionRequest request) {
        UserAccount user = userService.getRequiredUser(email);
        MeditationSession session = meditationSessionRepository.save(MeditationSession.builder()
                .user(user)
                .minutes(request.minutes())
                .notes(request.notes())
                .completedAt(request.completedAt() == null ? LocalDateTime.now() : request.completedAt())
                .build());
        gamificationService.awardExperience(user, 20);
        return toDto(session);
    }

    @Transactional(readOnly = true)
    public List<MeditationSessionResponse> list(String email) {
        UserAccount user = userService.getRequiredUser(email);
        return meditationSessionRepository.findByUserOrderByCompletedAtDesc(user).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public MeditationStatsResponse stats(String email) {
        UserAccount user = userService.getRequiredUser(email);
        List<MeditationSession> sessions = meditationSessionRepository.findByUserOrderByCompletedAtDesc(user);
        long totalMinutes = sessions.stream().mapToLong(MeditationSession::getMinutes).sum();
        return new MeditationStatsResponse(totalMinutes, sessions.size(), calculateStreak(sessions));
    }

    private int calculateStreak(List<MeditationSession> sessions) {
        List<LocalDate> dates = sessions.stream().map(session -> session.getCompletedAt().toLocalDate()).distinct().toList();
        int streak = 0;
        LocalDate cursor = LocalDate.now();
        for (LocalDate date : dates) {
            if (date.equals(cursor) || (streak == 0 && date.equals(cursor.minusDays(1)))) {
                streak++;
                cursor = date.minusDays(1);
            } else if (streak > 0 && date.equals(cursor)) {
                streak++;
                cursor = cursor.minusDays(1);
            } else if (streak > 0) {
                break;
            }
        }
        return streak;
    }

    private MeditationSessionResponse toDto(MeditationSession session) {
        return new MeditationSessionResponse(session.getId(), session.getMinutes(), session.getNotes(), session.getCompletedAt());
    }
}
