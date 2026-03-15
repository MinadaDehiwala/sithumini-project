package com.aurabloom.service;

import com.aurabloom.dto.AdminUserResponse;
import com.aurabloom.dto.BadgeResponse;
import com.aurabloom.dto.RoleUpdateRequest;
import com.aurabloom.dto.UpdateProfileRequest;
import com.aurabloom.dto.UserProfileResponse;
import com.aurabloom.entity.UserAccount;
import com.aurabloom.exception.ApiException;
import com.aurabloom.repository.UserAccountRepository;
import com.aurabloom.repository.UserBadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserAccountRepository userAccountRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final PasswordEncoder passwordEncoder;
    private final GamificationService gamificationService;

    @Transactional(readOnly = true)
    public UserAccount getRequiredUser(String email) {
        return userAccountRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String email) {
        UserAccount user = getRequiredUser(email);
        gamificationService.syncBadges(user);
        return toProfile(user);
    }

    public UserProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        UserAccount user = getRequiredUser(email);
        if (request.fullName() != null && !request.fullName().isBlank()) {
            user.setFullName(request.fullName().trim());
        }
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        return toProfile(userAccountRepository.save(user));
    }

    public void deleteProfile(String email) {
        userAccountRepository.delete(getRequiredUser(email));
    }

    @Transactional(readOnly = true)
    public List<AdminUserResponse> listUsers() {
        return userAccountRepository.findAll().stream()
                .map(this::toAdmin)
                .toList();
    }

    public AdminUserResponse updateUser(Long id, RoleUpdateRequest request) {
        UserAccount user = userAccountRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        user.setRole(request.role());
        user.setActive(request.active());
        return toAdmin(userAccountRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserProfileResponse toProfile(UserAccount user) {
        List<BadgeResponse> badges = userBadgeRepository.findByUserOrderByCreatedAtAsc(user).stream()
                .map(userBadge -> new BadgeResponse(
                        userBadge.getBadge().getCode(),
                        userBadge.getBadge().getName(),
                        userBadge.getBadge().getDescription(),
                        userBadge.getCreatedAt()
                ))
                .toList();

        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.isActive(),
                user.getLevel(),
                user.getExperiencePoints(),
                badges
        );
    }

    private AdminUserResponse toAdmin(UserAccount user) {
        return new AdminUserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.isActive(),
                user.getLevel(),
                user.getExperiencePoints()
        );
    }
}
