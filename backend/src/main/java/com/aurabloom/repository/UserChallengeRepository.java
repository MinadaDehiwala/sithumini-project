package com.aurabloom.repository;

import com.aurabloom.entity.ChallengeStatus;
import com.aurabloom.entity.UserAccount;
import com.aurabloom.entity.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {
    Optional<UserChallenge> findByUserAndAssignedDate(UserAccount user, LocalDate assignedDate);
    List<UserChallenge> findByUserOrderByAssignedDateDesc(UserAccount user);
    List<UserChallenge> findByUserAndAssignedDateBetweenOrderByAssignedDateAsc(UserAccount user, LocalDate start, LocalDate end);
    long countByUserAndStatus(UserAccount user, ChallengeStatus status);
}
