package com.aurabloom.dto;

import com.aurabloom.entity.RecommendationCategory;
import com.aurabloom.entity.RiskLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RecommendationTemplateRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull RecommendationCategory category,
        @NotNull RiskLevel targetRiskLevel,
        boolean active
) {
}
