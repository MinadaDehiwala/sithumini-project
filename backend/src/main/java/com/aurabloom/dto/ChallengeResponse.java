package com.aurabloom.dto;

import com.aurabloom.entity.ChallengeStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ChallengeResponse(
        Long id,
        String title,
        String description,
        String category,
        int rewardXp,
        LocalDate assignedDate,
        ChallengeStatus status,
        LocalDateTime completedAt
) {
}
