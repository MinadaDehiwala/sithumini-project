package com.aurabloom.dto;

import com.aurabloom.entity.MoodType;

import java.time.LocalDate;

public record MoodEntryResponse(
        Long id,
        MoodType moodType,
        String note,
        LocalDate entryDate
) {
}
