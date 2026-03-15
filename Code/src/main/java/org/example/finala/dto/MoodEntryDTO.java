package org.example.finala.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MoodEntryDTO {

    @NotNull(message = "Mood type is required")
    private String moodType;

    private String note;
}