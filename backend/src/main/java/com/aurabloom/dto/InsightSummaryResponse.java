package com.aurabloom.dto;

import com.aurabloom.entity.RiskLevel;

import java.util.List;

public record InsightSummaryResponse(
        int score,
        RiskLevel riskLevel,
        RiskBreakdownResponse breakdown,
        String supportMessage,
        List<RecommendationResponse> recommendations,
        long unlockedCapsuleCount,
        int currentLevel,
        int experiencePoints,
        List<BadgeResponse> badges
) {
}
