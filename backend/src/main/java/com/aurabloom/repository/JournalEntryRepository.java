package com.aurabloom.repository;

import com.aurabloom.entity.JournalEntry;
import com.aurabloom.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
    List<JournalEntry> findByUserOrderByEntryDateDescCreatedAtDesc(UserAccount user);
    List<JournalEntry> findByUserAndEntryDateBetweenOrderByEntryDateAsc(UserAccount user, LocalDate start, LocalDate end);
    long countByUser(UserAccount user);
}
