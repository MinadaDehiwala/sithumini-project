package org.example.finala.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoodTrendDTO {

    private LocalDate startDate;
    private LocalDate endDate;
    private Map<String, Long> moodCounts;

}