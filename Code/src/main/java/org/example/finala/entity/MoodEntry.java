package org.example.finala.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "mood_entries")
public class MoodEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Mood type is required")
    @Enumerated(EnumType.STRING)
    private MoodType moodType;

    private String note;

    private LocalDate createdDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}