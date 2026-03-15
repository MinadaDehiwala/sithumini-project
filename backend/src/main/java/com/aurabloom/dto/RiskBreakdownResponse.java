package com.aurabloom.dto;

public record RiskBreakdownResponse(
        int moodContribution,
        int journalContribution,
        int meditationContribution,
        int challengeContribution
) {
}
