package com.aurabloom.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommunityPostRequest(
        @NotBlank @Size(max = 3000) String body
) {
}
