package com.aurabloom.dto;

import com.aurabloom.entity.MoodType;

import java.time.LocalDate;
import java.util.Map;

public record MoodTrendResponse(
        LocalDate periodStart,
        LocalDate periodEnd,
        Map<MoodType, Long> counts
) {
}
