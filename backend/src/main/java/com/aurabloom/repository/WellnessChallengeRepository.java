package com.aurabloom.repository;

import com.aurabloom.entity.WellnessChallenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WellnessChallengeRepository extends JpaRepository<WellnessChallenge, Long> {
    List<WellnessChallenge> findByActiveTrueOrderByIdAsc();
}
