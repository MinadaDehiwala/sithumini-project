package com.aurabloom.dto;

public record ChallengeTemplateResponse(
        Long id,
        String title,
        String description,
        String category,
        int rewardXp,
        boolean active
) {
}
