package com.aurabloom.dto;

import java.time.LocalDateTime;

public record MeditationSessionResponse(
        Long id,
        int minutes,
        String notes,
        LocalDateTime completedAt
) {
}
