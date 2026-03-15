package com.aurabloom.repository;

import com.aurabloom.entity.MoodEntry;
import com.aurabloom.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {
    List<MoodEntry> findByUserOrderByEntryDateDescCreatedAtDesc(UserAccount user);
    List<MoodEntry> findByUserAndEntryDateBetweenOrderByEntryDateAsc(UserAccount user, LocalDate start, LocalDate end);
    long countByUser(UserAccount user);
}
