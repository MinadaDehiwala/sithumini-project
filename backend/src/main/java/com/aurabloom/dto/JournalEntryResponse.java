package com.aurabloom.dto;

import com.aurabloom.entity.JournalSentiment;

import java.time.LocalDate;
import java.util.List;

public record JournalEntryResponse(
        Long id,
        String title,
        String body,
        LocalDate entryDate,
        JournalSentiment sentiment,
        int sentimentScore,
        List<String> keywords
) {
}
