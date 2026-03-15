package com.aurabloom.dto;

import java.time.LocalDateTime;

public record BadgeResponse(
        String code,
        String name,
        String description,
        LocalDateTime awardedAt
) {
}
