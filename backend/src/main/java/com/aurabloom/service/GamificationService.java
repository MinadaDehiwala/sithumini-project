package com.aurabloom.service;

import com.aurabloom.entity.BadgeDefinition;
import com.aurabloom.entity.BadgeMetric;
import com.aurabloom.entity.ChallengeStatus;
import com.aurabloom.entity.UserAccount;
import com.aurabloom.entity.UserBadge;
import com.aurabloom.repository.BadgeDefinitionRepository;
import com.aurabloom.repository.JournalEntryRepository;
import com.aurabloom.repository.MeditationSessionRepository;
import com.aurabloom.repository.MoodEntryRepository;
import com.aurabloom.repository.UserAccountRepository;
import com.aurabloom.repository.UserBadgeRepository;
import com.aurabloom.repository.UserChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class GamificationService {

    private final UserAccountRepository userAccountRepository;
    private final MoodEntryRepository moodEntryRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final MeditationSessionRepository meditationSessionRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final BadgeDefinitionRepository badgeDefinitionRepository;
    private final UserBadgeRepository userBadgeRepository;

    public void awardExperience(UserAccount user, int xp) {
        user.setExperiencePoints(user.getExperiencePoints() + xp);
        user.setLevel(calculateLevel(user.getExperiencePoints()));
        userAccountRepository.save(user);
        syncBadges(user);
    }

    public void syncBadges(UserAccount user) {
        Map<BadgeMetric, Integer> metrics = new EnumMap<>(BadgeMetric.class);
        metrics.put(BadgeMetric.MOODS_LOGGED, Math.toIntExact(moodEntryRepository.countByUser(user)));
        metrics.put(BadgeMetric.JOURNALS_WRITTEN, Math.toIntExact(journalEntryRepository.countByUser(user)));
        metrics.put(BadgeMetric.MEDITATIONS_COMPLETED, Math.toIntExact(meditationSessionRepository.countByUser(user)));
        metrics.put(BadgeMetric.CHALLENGES_COMPLETED, Math.toIntExact(userChallengeRepository.countByUserAndStatus(user, ChallengeStatus.COMPLETED)));
        metrics.put(BadgeMetric.CURRENT_STREAK, calculateChallengeStreak(user));

        for (BadgeDefinition definition : badgeDefinitionRepository.findAll()) {
            int metricValue = metrics.getOrDefault(definition.getMetric(), 0);
            if (metricValue >= definition.getThresholdValue() && !userBadgeRepository.existsByUserAndBadge(user, definition)) {
                userBadgeRepository.save(UserBadge.builder()
                        .user(user)
                        .badge(definition)
                        .build());
            }
        }
    }

    private int calculateLevel(int experiencePoints) {
        return 1 + (experiencePoints / 100);
    }

    private int calculateChallengeStreak(UserAccount user) {
        List<LocalDate> completedDates = userChallengeRepository.findByUserOrderByAssignedDateDesc(user).stream()
                .filter(challenge -> challenge.getStatus() == ChallengeStatus.COMPLETED)
                .map(challenge -> challenge.getAssignedDate())
                .distinct()
                .toList();

        int streak = 0;
        LocalDate cursor = LocalDate.now();
        for (LocalDate date : completedDates) {
            if (date.equals(cursor) || date.equals(cursor.minusDays(1)) || streak > 0 && date.equals(cursor)) {
                streak++;
                cursor = date.minusDays(1);
            } else if (streak == 0 && date.equals(LocalDate.now().minusDays(1))) {
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
}
