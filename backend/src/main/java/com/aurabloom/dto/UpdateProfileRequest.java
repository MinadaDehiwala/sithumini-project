package com.aurabloom.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        String fullName,
        @Size(min = 8, max = 120) String password
) {
}
