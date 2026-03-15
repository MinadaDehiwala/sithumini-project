package org.example.finala.repository;

import org.example.finala.entity.MoodEntry;
import org.example.finala.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {

    List<MoodEntry> findByUser(User user);

    List<MoodEntry> findByUserAndCreatedDateBetween(User user, LocalDate start, LocalDate end);

    List<MoodEntry> findByUserAndCreatedDateAfter(User user, LocalDate after);

}