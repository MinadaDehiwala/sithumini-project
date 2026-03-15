package com.aurabloom.config;

import com.aurabloom.entity.BadgeDefinition;
import com.aurabloom.entity.BadgeMetric;
import com.aurabloom.entity.RecommendationCategory;
import com.aurabloom.entity.RiskLevel;
import com.aurabloom.entity.Role;
import com.aurabloom.entity.UserAccount;
import com.aurabloom.entity.WellnessChallenge;
import com.aurabloom.entity.WellnessRecommendationTemplate;
import com.aurabloom.repository.BadgeDefinitionRepository;
import com.aurabloom.repository.UserAccountRepository;
import com.aurabloom.repository.WellnessChallengeRepository;
import com.aurabloom.repository.WellnessRecommendationTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final BadgeDefinitionRepository badgeDefinitionRepository;
    private final WellnessChallengeRepository wellnessChallengeRepository;
    private final WellnessRecommendationTemplateRepository recommendationTemplateRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedBadges();
        seedChallenges();
        seedRecommendations();
        seedAdmin();
    }

    private void seedBadges() {
        if (badgeDefinitionRepository.count() > 0) {
            return;
        }
        badgeDefinitionRepository.saveAll(List.of(
                BadgeDefinition.builder().code("FIRST_CHECKIN").name("First Check-In").description("Logged your first mood.").metric(BadgeMetric.MOODS_LOGGED).thresholdValue(1).build(),
                BadgeDefinition.builder().code("REFLECTION_STARTER").name("Reflection Starter").description("Wrote three journal entries.").metric(BadgeMetric.JOURNALS_WRITTEN).thresholdValue(3).build(),
                BadgeDefinition.builder().code("BREATHWORK_BUILDER").name("Breathwork Builder").description("Completed five meditation sessions.").metric(BadgeMetric.MEDITATIONS_COMPLETED).thresholdValue(5).build(),
                BadgeDefinition.builder().code("CHALLENGE_CHAMP").name("Challenge Champ").description("Completed seven wellness challenges.").metric(BadgeMetric.CHALLENGES_COMPLETED).thresholdValue(7).build(),
                BadgeDefinition.builder().code("CONSISTENT_CARE").name("Consistent Care").description("Maintained a three-day challenge streak.").metric(BadgeMetric.CURRENT_STREAK).thresholdValue(3).build()
        ));
    }

    private void seedChallenges() {
        if (wellnessChallengeRepository.count() > 0) {
            return;
        }
        wellnessChallengeRepository.saveAll(List.of(
                WellnessChallenge.builder().title("Morning Breath Reset").description("Spend five quiet minutes breathing slowly before opening any apps.").category("Mindfulness").rewardXp(25).active(true).build(),
                WellnessChallenge.builder().title("Kindness Note").description("Write one kind sentence to yourself in your journal today.").category("Reflection").rewardXp(25).active(true).build(),
                WellnessChallenge.builder().title("Walk and Notice").description("Take a ten-minute walk and write down three details you noticed around you.").category("Movement").rewardXp(30).active(true).build(),
                WellnessChallenge.builder().title("Hydration Pause").description("Use one break to drink water and take ten deep breaths.").category("Habits").rewardXp(20).active(true).build(),
                WellnessChallenge.builder().title("Reach Out").description("Send one supportive message to someone you trust.").category("Connection").rewardXp(30).active(true).build()
        ));
    }

    private void seedRecommendations() {
        if (recommendationTemplateRepository.count() > 0) {
            return;
        }
        recommendationTemplateRepository.saveAll(List.of(
                WellnessRecommendationTemplate.builder().title("Two-Minute Grounding").description("Pause and name five things you can see, four you can feel, and three you can hear.").category(RecommendationCategory.GROUNDING).targetRiskLevel(RiskLevel.HIGH).active(true).build(),
                WellnessRecommendationTemplate.builder().title("Short Reflection Prompt").description("Journal about what felt heavy today and one thing you still have control over.").category(RecommendationCategory.REFLECTION).targetRiskLevel(RiskLevel.HIGH).active(true).build(),
                WellnessRecommendationTemplate.builder().title("Guided Reset").description("Complete a 10-minute meditation session to lower tension and reset your pace.").category(RecommendationCategory.MEDITATION).targetRiskLevel(RiskLevel.HIGH).active(true).build(),
                WellnessRecommendationTemplate.builder().title("Gentle Challenge").description("Choose one small action from today’s challenge and finish it before the evening.").category(RecommendationCategory.CHALLENGE).targetRiskLevel(RiskLevel.MODERATE).active(true).build(),
                WellnessRecommendationTemplate.builder().title("Stay Connected").description("Leave a supportive anonymous note in the community or talk to someone you trust.").category(RecommendationCategory.CONNECTION).targetRiskLevel(RiskLevel.MODERATE).active(true).build(),
                WellnessRecommendationTemplate.builder().title("Keep Your Rhythm").description("You are showing a stable pattern. Keep tracking and add one calming habit today.").category(RecommendationCategory.GROUNDING).targetRiskLevel(RiskLevel.LOW).active(true).build()
        ));
    }

    private void seedAdmin() {
        if (!appProperties.getBootstrap().isCreateDefaultAdmin() ||
                userAccountRepository.existsByEmailIgnoreCase(appProperties.getBootstrap().getAdminEmail())) {
            return;
        }
        userAccountRepository.save(UserAccount.builder()
                .email(appProperties.getBootstrap().getAdminEmail().toLowerCase())
                .fullName(appProperties.getBootstrap().getAdminName())
                .passwordHash(passwordEncoder.encode(appProperties.getBootstrap().getAdminPassword()))
                .role(Role.ADMIN)
                .active(true)
                .experiencePoints(0)
                .level(1)
                .build());
    }
}
