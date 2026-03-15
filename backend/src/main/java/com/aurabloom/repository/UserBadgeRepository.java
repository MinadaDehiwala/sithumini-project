package com.aurabloom.repository;

import com.aurabloom.entity.BadgeDefinition;
import com.aurabloom.entity.UserAccount;
import com.aurabloom.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    List<UserBadge> findByUserOrderByCreatedAtAsc(UserAccount user);
    boolean existsByUserAndBadge(UserAccount user, BadgeDefinition badge);
}
