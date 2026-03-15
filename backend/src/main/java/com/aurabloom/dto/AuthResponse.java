package com.aurabloom.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        long expiresInSeconds,
        UserProfileResponse user
) {
}
