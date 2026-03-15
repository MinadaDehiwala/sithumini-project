package com.aurabloom.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record TimeCapsuleRequest(
        @NotBlank String title,
        @NotBlank String message,
        @Future LocalDateTime unlockAt
) {
}
