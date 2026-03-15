package com.aurabloom.repository;

import com.aurabloom.entity.MeditationSession;
import com.aurabloom.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MeditationSessionRepository extends JpaRepository<MeditationSession, Long> {
    List<MeditationSession> findByUserOrderByCompletedAtDesc(UserAccount user);
    List<MeditationSession> findByUserAndCompletedAtBetweenOrderByCompletedAtAsc(UserAccount user, LocalDateTime start, LocalDateTime end);
    long countByUser(UserAccount user);
}
