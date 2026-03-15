package com.aurabloom.service;

import com.aurabloom.dto.TimeCapsuleRequest;
import com.aurabloom.dto.TimeCapsuleResponse;
import com.aurabloom.entity.TimeCapsuleMessage;
import com.aurabloom.entity.UserAccount;
import com.aurabloom.repository.TimeCapsuleMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TimeCapsuleService {

    private final TimeCapsuleMessageRepository timeCapsuleMessageRepository;
    private final UserService userService;

    public TimeCapsuleResponse create(String email, TimeCapsuleRequest request) {
        UserAccount user = userService.getRequiredUser(email);
        TimeCapsuleMessage capsule = timeCapsuleMessageRepository.save(TimeCapsuleMessage.builder()
                .user(user)
                .title(request.title())
                .message(request.message())
                .unlockAt(request.unlockAt())
                .build());
        return toResponse(capsule, false);
    }

    public List<TimeCapsuleResponse> list(String email) {
        UserAccount user = userService.getRequiredUser(email);
        LocalDateTime now = LocalDateTime.now();
        List<TimeCapsuleMessage> capsules = timeCapsuleMessageRepository.findByUserOrderByUnlockAtDesc(user);
        capsules.stream()
                .filter(capsule -> capsule.getUnlockAt().isBefore(now) || capsule.getUnlockAt().isEqual(now))
                .filter(capsule -> capsule.getFirstUnlockedAt() == null)
                .forEach(capsule -> capsule.setFirstUnlockedAt(now));
        return capsules.stream()
                .map(capsule -> toResponse(capsule, capsule.getUnlockAt().isBefore(now) || capsule.getUnlockAt().isEqual(now)))
                .toList();
    }

    @Transactional(readOnly = true)
    public long countUnlocked(UserAccount user) {
        return timeCapsuleMessageRepository.findByUserAndUnlockAtLessThanEqualOrderByUnlockAtDesc(user, LocalDateTime.now()).size();
    }

    private TimeCapsuleResponse toResponse(TimeCapsuleMessage capsule, boolean unlocked) {
        return new TimeCapsuleResponse(
                capsule.getId(),
                capsule.getTitle(),
                unlocked ? capsule.getMessage() : null,
                capsule.getUnlockAt(),
                unlocked
        );
    }
}
