package com.aurabloom.dto;

import com.aurabloom.entity.Role;

public record AdminUserResponse(
        Long id,
        String email,
        String fullName,
        Role role,
        boolean active,
        int level,
        int experiencePoints
) {
}
