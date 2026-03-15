package com.aurabloom.dto;

public record MeditationStatsResponse(
        long totalMinutes,
        long totalSessions,
        int currentStreak
) {
}
