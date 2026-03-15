package com.aurabloom.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "recommendation_templates")
public class WellnessRecommendationTemplate extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecommendationCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel targetRiskLevel;

    @Column(nullable = false)
    private boolean active;
}
