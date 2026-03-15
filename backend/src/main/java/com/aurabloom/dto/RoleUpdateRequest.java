package com.aurabloom.dto;

import com.aurabloom.entity.Role;
import jakarta.validation.constraints.NotNull;

public record RoleUpdateRequest(
        @NotNull Role role,
        @NotNull Boolean active
) {
}
