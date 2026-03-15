package com.aurabloom.dto;

import java.time.LocalDateTime;

public record TimeCapsuleResponse(
        Long id,
        String title,
        String message,
        LocalDateTime unlockAt,
        boolean unlocked
) {
}
