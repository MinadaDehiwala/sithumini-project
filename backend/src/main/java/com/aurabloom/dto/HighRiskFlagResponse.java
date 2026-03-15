package com.aurabloom.dto;

import com.aurabloom.entity.RiskLevel;

import java.time.LocalDateTime;

public record HighRiskFlagResponse(
        Long id,
        Long userId,
        String userName,
        String userEmail,
        RiskLevel riskLevel,
        int score,
        String summary,
        boolean resolved,
        LocalDateTime createdAt
) {
}
