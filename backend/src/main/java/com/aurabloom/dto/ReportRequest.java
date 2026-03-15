package com.aurabloom.dto;

import jakarta.validation.constraints.NotBlank;

public record ReportRequest(
        @NotBlank String reason
) {
}
