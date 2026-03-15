package com.aurabloom.service;

import com.aurabloom.dto.InsightSummaryResponse;
import com.aurabloom.dto.RiskBreakdownResponse;
import com.aurabloom.dto.RecommendationResponse;
import com.aurabloom.entity.ChallengeStatus;
import com.aurabloom.entity.HighRiskFlag;
import com.aurabloom.entity.JournalEntry;
import com.aurabloom.entity.JournalSentiment;
import com.aurabloom.entity.MeditationSession;
import com.aurabloom.entity.MoodEntry;
import com.aurabloom.entity.MoodType;
import com.aurabloom.entity.RecommendationCategory;
import com.aurabloom.entity.RiskLevel;
import com.aurabloom.entity.UserAccount;
import com.aurabloom.entity.UserChallenge;
import com.aurabloom.entity.WellnessRecommendationTemplate;
import com.aurabloom.repository.HighRiskFlagRepository;
import com.aurabloom.repository.JournalEntryRepository;
import com.aurabloom.repository.MeditationSessionRepository;
import com.aurabloom.repository.MoodEntryRepository;
import com.aurabloom.repository.UserChallengeRepository;
import com.aurabloom.repository.WellnessRecommendationTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class InsightsService {

    private final MoodEntryRepository moodEntryRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final MeditationSessionRepository meditationSessionRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final WellnessRecommendationTemplateRepository recommendationTemplateRepository;
    private final HighRiskFlagRepository highRiskFlagRepository;
    private final UserService userService;
    private final GamificationService gamificationService;
    private final TimeCapsuleService timeCapsuleService;

    public InsightSummaryResponse summary(String email) {
        UserAccount user = userService.getRequiredUser(email);
        gamificationService.syncBadges(user);

        RiskAnalysis analysis = analyze(user);
        persistFlagIfNeeded(user, analysis);

        return new InsightSummaryResponse(
                analysis.score(),
                analysis.riskLevel(),
                new RiskBreakdownResponse(
                        analysis.moodContribution(),
                        analysis.journalContribution(),
                        analysis.meditationContribution(),
                        analysis.challengeContribution()
                ),
                supportMessage(analysis.riskLevel()),
                recommendedTemplates(analysis).stream()
                        .map(template -> new RecommendationResponse(
                                template.getId(),
                                template.getTitle(),
                                template.getDescription(),
                                template.getCategory()
                        ))
                        .toList(),
                timeCapsuleService.countUnlocked(user),
                user.getLevel(),
                user.getExperiencePoints(),
                userService.toProfile(user).badges()
        );
    }

    private RiskAnalysis analyze(UserAccount user) {
        LocalDate start = LocalDate.now().minusDays(13);
        LocalDateTime sessionStart = start.atStartOfDay();

        List<MoodEntry> moods = moodEntryRepository.findByUserAndEntryDateBetweenOrderByEntryDateAsc(user, start, LocalDate.now());
        List<JournalEntry> journals = journalEntryRepository.findByUserAndEntryDateBetweenOrderByEntryDateAsc(user, start, LocalDate.now());
        List<MeditationSession> meditations = meditationSessionRepository.findByUserAndCompletedAtBetweenOrderByCompletedAtAsc(user, sessionStart, LocalDateTime.now());
        List<UserChallenge> challenges = userChallengeRepository.findByUserAndAssignedDateBetweenOrderByAssignedDateAsc(user, start, LocalDate.now());

        int negativeMoods = (int) moods.stream()
                .filter(mood -> mood.getMoodType() == MoodType.SAD || mood.getMoodType() == MoodType.STRESSED || mood.getMoodType() == MoodType.ANXIOUS)
                .count();
        int negativeJournals = (int) journals.stream()
                .filter(journal -> journal.getSentiment() == JournalSentiment.NEGATIVE)
                .count();
        int meditationCount = meditations.size();
        int completedChallenges = (int) challenges.stream()
                .filter(challenge -> challenge.getStatus() == ChallengeStatus.COMPLETED)
                .count();

        int moodContribution = moods.isEmpty() ? 0 : Math.round((negativeMoods * 100f / moods.size()) * 0.40f);
        int journalContribution = journals.isEmpty() ? 0 : Math.round((negativeJournals * 100f / journals.size()) * 0.30f);
        int meditationContribution = Math.round((Math.max(0, 4 - meditationCount) * 100f / 4f) * 0.15f);
        int challengeContribution = Math.round((Math.max(0, 7 - completedChallenges) * 100f / 7f) * 0.15f);

        int score = Math.min(100, moodContribution + journalContribution + meditationContribution + challengeContribution);
        RiskLevel riskLevel = score >= 60 ? RiskLevel.HIGH : score >= 30 ? RiskLevel.MODERATE : RiskLevel.LOW;
        return new RiskAnalysis(score, riskLevel, moodContribution, journalContribution, meditationContribution, challengeContribution);
    }

    private void persistFlagIfNeeded(UserAccount user, RiskAnalysis analysis) {
        if (analysis.riskLevel() != RiskLevel.HIGH) {
            return;
        }
        if (highRiskFlagRepository.findTopByUserAndResolvedFalseOrderByCreatedAtDesc(user).isPresent()) {
            return;
        }
        highRiskFlagRepository.save(HighRiskFlag.builder()
                .user(user)
                .riskLevel(analysis.riskLevel())
                .score(analysis.score())
                .summary("High-risk pattern detected across mood, journaling, meditation, and challenge activity.")
                .resolved(false)
                .build());
    }

    private List<WellnessRecommendationTemplate> recommendedTemplates(RiskAnalysis analysis) {
        Set<RiskLevel> levels = analysis.riskLevel() == RiskLevel.HIGH
                ? EnumSet.of(RiskLevel.HIGH, RiskLevel.MODERATE)
                : EnumSet.of(analysis.riskLevel(), RiskLevel.LOW);
        List<WellnessRecommendationTemplate> candidates = recommendationTemplateRepository.findByActiveTrueAndTargetRiskLevelIn(levels);
        if (candidates.isEmpty()) {
            return List.of();
        }

        List<RecommendationCategory> preferredCategories = new ArrayList<>();
        if (analysis.moodContribution() >= analysis.journalContribution()) {
            preferredCategories.add(RecommendationCategory.GROUNDING);
        }
        if (analysis.journalContribution() > 0) {
            preferredCategories.add(RecommendationCategory.REFLECTION);
        }
        if (analysis.meditationContribution() > 0) {
            preferredCategories.add(RecommendationCategory.MEDITATION);
        }
        if (analysis.challengeContribution() > 0) {
            preferredCategories.add(RecommendationCategory.CHALLENGE);
        }
        preferredCategories.add(RecommendationCategory.CONNECTION);

        List<WellnessRecommendationTemplate> selected = new ArrayList<>();
        for (RecommendationCategory category : preferredCategories) {
            candidates.stream()
                    .filter(template -> template.getCategory() == category)
                    .filter(template -> selected.stream().noneMatch(existing -> existing.getId().equals(template.getId())))
                    .findFirst()
                    .ifPresent(selected::add);
            if (selected.size() == 3) {
                return selected;
            }
        }

        for (WellnessRecommendationTemplate candidate : candidates) {
            if (selected.stream().noneMatch(existing -> existing.getId().equals(candidate.getId()))) {
                selected.add(candidate);
            }
            if (selected.size() == 3) {
                break;
            }
        }

        return selected;
    }

    private String supportMessage(RiskLevel riskLevel) {
        return switch (riskLevel) {
            case HIGH -> "Your recent activity suggests you may need extra support. Try a calming session, a short reflection, and one gentle challenge today.";
            case MODERATE -> "Your patterns show some strain. A mindful reset and a small wellness step can help you steady the next few days.";
            case LOW -> "You are showing a steady pattern. Keep the momentum with one reflective action and one restorative habit.";
        };
    }

    public record RiskAnalysis(
            int score,
            RiskLevel riskLevel,
            int moodContribution,
            int journalContribution,
            int meditationContribution,
            int challengeContribution
    ) {
    }
}
