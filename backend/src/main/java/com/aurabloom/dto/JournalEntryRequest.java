package com.aurabloom.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record JournalEntryRequest(
        @NotBlank String title,
        @NotBlank String body,
        LocalDate entryDate
) {
}
