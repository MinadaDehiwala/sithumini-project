package com.aurabloom.repository;

import com.aurabloom.entity.TimeCapsuleMessage;
import com.aurabloom.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TimeCapsuleMessageRepository extends JpaRepository<TimeCapsuleMessage, Long> {
    List<TimeCapsuleMessage> findByUserOrderByUnlockAtDesc(UserAccount user);
    List<TimeCapsuleMessage> findByUserAndUnlockAtLessThanEqualOrderByUnlockAtDesc(UserAccount user, LocalDateTime now);
}
