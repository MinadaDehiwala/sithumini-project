package com.aurabloom.dto;

import com.aurabloom.entity.Role;

import java.util.List;

public record UserProfileResponse(
        Long id,
        String email,
        String fullName,
        Role role,
        boolean active,
        int level,
        int experiencePoints,
        List<BadgeResponse> badges
) {
}
