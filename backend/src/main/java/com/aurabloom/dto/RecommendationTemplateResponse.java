package com.aurabloom.dto;

import com.aurabloom.entity.RecommendationCategory;
import com.aurabloom.entity.RiskLevel;

public record RecommendationTemplateResponse(
        Long id,
        String title,
        String description,
        RecommendationCategory category,
        RiskLevel targetRiskLevel,
        boolean active
) {
}
