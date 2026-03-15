package com.aurabloom.dto;

import com.aurabloom.entity.MoodType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record MoodEntryRequest(
        @NotNull MoodType moodType,
        @Size(max = 500) String note,
        LocalDate entryDate
) {
}
