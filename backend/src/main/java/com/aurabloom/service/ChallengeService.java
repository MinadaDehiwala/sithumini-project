package com.aurabloom.service;

import com.aurabloom.dto.ChallengeResponse;
import com.aurabloom.dto.ChallengeTemplateRequest;
import com.aurabloom.dto.ChallengeTemplateResponse;
import com.aurabloom.entity.ChallengeStatus;
import com.aurabloom.entity.UserAccount;
import com.aurabloom.entity.UserChallenge;
import com.aurabloom.entity.WellnessChallenge;
import com.aurabloom.exception.ApiException;
import com.aurabloom.repository.UserChallengeRepository;
import com.aurabloom.repository.WellnessChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChallengeService {

    private final WellnessChallengeRepository wellnessChallengeRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final UserService userService;
    private final GamificationService gamificationService;

    public ChallengeResponse getTodayChallenge(String email) {
        UserAccount user = userService.getRequiredUser(email);
        return toResponse(ensureTodayAssignment(user));
    }

    @Transactional(readOnly = true)
    public List<ChallengeResponse> history(String email) {
        UserAccount user = userService.getRequiredUser(email);
        return userChallengeRepository.findByUserOrderByAssignedDateDesc(user).stream()
                .map(this::toResponse)
                .toList();
    }

    public ChallengeResponse completeToday(String email) {
        UserAccount user = userService.getRequiredUser(email);
        UserChallenge challenge = ensureTodayAssignment(user);
        if (challenge.getStatus() != ChallengeStatus.COMPLETED) {
            challenge.setStatus(ChallengeStatus.COMPLETED);
            challenge.setCompletedAt(LocalDateTime.now());
            userChallengeRepository.save(challenge);
            gamificationService.awardExperience(user, challenge.getChallenge().getRewardXp());
        }
        return toResponse(challenge);
    }

    @Transactional(readOnly = true)
    public List<ChallengeTemplateResponse> listTemplates() {
        return wellnessChallengeRepository.findAll().stream().map(this::toTemplate).toList();
    }

    public ChallengeTemplateResponse createTemplate(ChallengeTemplateRequest request) {
        WellnessChallenge challenge = wellnessChallengeRepository.save(WellnessChallenge.builder()
                .title(request.title())
                .description(request.description())
                .category(request.category())
                .rewardXp(request.rewardXp())
                .active(request.active())
                .build());
        return toTemplate(challenge);
    }

    public ChallengeTemplateResponse updateTemplate(Long id, ChallengeTemplateRequest request) {
        WellnessChallenge challenge = wellnessChallengeRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Challenge template not found"));
        challenge.setTitle(request.title());
        challenge.setDescription(request.description());
        challenge.setCategory(request.category());
        challenge.setRewardXp(request.rewardXp());
        challenge.setActive(request.active());
        return toTemplate(wellnessChallengeRepository.save(challenge));
    }

    private UserChallenge ensureTodayAssignment(UserAccount user) {
        LocalDate today = LocalDate.now();
        return userChallengeRepository.findByUserAndAssignedDate(user, today)
                .orElseGet(() -> {
                    List<WellnessChallenge> activeChallenges = wellnessChallengeRepository.findByActiveTrueOrderByIdAsc();
                    if (activeChallenges.isEmpty()) {
                        throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "No active challenges are configured");
                    }
                    int index = Math.floorMod((int) (user.getId() + today.toEpochDay()), activeChallenges.size());
                    WellnessChallenge selected = activeChallenges.get(index);
                    return userChallengeRepository.save(UserChallenge.builder()
                            .user(user)
                            .challenge(selected)
                            .assignedDate(today)
                            .status(ChallengeStatus.ASSIGNED)
                            .build());
                });
    }

    private ChallengeResponse toResponse(UserChallenge challenge) {
        return new ChallengeResponse(
                challenge.getId(),
                challenge.getChallenge().getTitle(),
                challenge.getChallenge().getDescription(),
                challenge.getChallenge().getCategory(),
                challenge.getChallenge().getRewardXp(),
                challenge.getAssignedDate(),
                challenge.getStatus(),
                challenge.getCompletedAt()
        );
    }

    private ChallengeTemplateResponse toTemplate(WellnessChallenge challenge) {
        return new ChallengeTemplateResponse(
                challenge.getId(),
                challenge.getTitle(),
                challenge.getDescription(),
                challenge.getCategory(),
                challenge.getRewardXp(),
                challenge.isActive()
        );
    }
}
