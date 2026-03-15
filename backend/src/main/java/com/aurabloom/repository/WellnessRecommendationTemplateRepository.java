package com.aurabloom.repository;

import com.aurabloom.entity.RiskLevel;
import com.aurabloom.entity.WellnessRecommendationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface WellnessRecommendationTemplateRepository extends JpaRepository<WellnessRecommendationTemplate, Long> {
    List<WellnessRecommendationTemplate> findByActiveTrueAndTargetRiskLevelIn(Collection<RiskLevel> levels);
}
