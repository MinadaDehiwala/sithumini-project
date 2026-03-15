package com.aurabloom.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ChallengeTemplateRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String category,
        @Min(1) int rewardXp,
        boolean active
) {
}
