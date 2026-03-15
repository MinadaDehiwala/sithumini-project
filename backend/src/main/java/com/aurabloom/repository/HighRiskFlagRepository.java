package com.aurabloom.repository;

import com.aurabloom.entity.HighRiskFlag;
import com.aurabloom.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HighRiskFlagRepository extends JpaRepository<HighRiskFlag, Long> {
    List<HighRiskFlag> findByResolvedFalseOrderByCreatedAtDesc();
    long countByResolvedFalse();
    Optional<HighRiskFlag> findTopByUserAndResolvedFalseOrderByCreatedAtDesc(UserAccount user);
}
