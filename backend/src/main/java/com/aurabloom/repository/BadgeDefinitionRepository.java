package com.aurabloom.repository;

import com.aurabloom.entity.BadgeDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BadgeDefinitionRepository extends JpaRepository<BadgeDefinition, Long> {
    Optional<BadgeDefinition> findByCode(String code);
}
