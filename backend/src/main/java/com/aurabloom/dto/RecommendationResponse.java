package com.aurabloom.dto;

import com.aurabloom.entity.RecommendationCategory;

public record RecommendationResponse(
        Long id,
        String title,
        String description,
        RecommendationCategory category
) {
}
