package com.aurabloom.dto;

public record AdminOverviewResponse(
        long totalUsers,
        long activeUsers,
        long reportedPosts,
        long openRiskFlags,
        long totalPosts,
        long totalChallenges
) {
}
