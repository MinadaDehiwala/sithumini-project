package com.aurabloom.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Email @NotBlank String email,
        @NotBlank String fullName,
        @NotBlank @Size(min = 8, max = 120) String password
) {
}
