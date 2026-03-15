package com.aurabloom.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record MeditationSessionRequest(
        @Min(1) @Max(240) int minutes,
        @Size(max = 500) String notes,
        LocalDateTime completedAt
) {
}
