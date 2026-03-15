package com.aurabloom.service;

import com.aurabloom.config.AppProperties;
import com.aurabloom.dto.AuthResponse;
import com.aurabloom.dto.LoginRequest;
import com.aurabloom.dto.PasswordResetRequest;
import com.aurabloom.dto.RefreshTokenRequest;
import com.aurabloom.dto.RegisterRequest;
import com.aurabloom.dto.UserProfileResponse;
import com.aurabloom.entity.RefreshToken;
import com.aurabloom.entity.Role;
import com.aurabloom.entity.UserAccount;
import com.aurabloom.exception.ApiException;
import com.aurabloom.repository.RefreshTokenRepository;
import com.aurabloom.repository.UserAccountRepository;
import com.aurabloom.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AppProperties appProperties;
    private final UserService userService;

    public UserProfileResponse register(RegisterRequest request) {
        if (userAccountRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email is already registered");
        }

        UserAccount user = UserAccount.builder()
                .email(request.email().trim().toLowerCase())
                .fullName(request.fullName().trim())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .active(true)
                .experiencePoints(0)
                .level(1)
                .build();

        return userService.toProfile(userAccountRepository.save(user));
    }

    public AuthResponse login(LoginRequest request) {
        UserAccount user = userAccountRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!user.isActive()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Account is inactive");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        return issueTokens(user);
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if (refreshToken.getRevokedAt() != null || refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token has expired");
        }

        refreshToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(refreshToken);

        return issueTokens(refreshToken.getUser());
    }

    public void requestPasswordReset(PasswordResetRequest request) {
        if (!appProperties.getMail().isEnabled()) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "Password reset email is not configured yet");
        }
        throw new ApiException(HttpStatus.NOT_IMPLEMENTED, "Password reset delivery is not enabled yet");
    }

    private AuthResponse issueTokens(UserAccount user) {
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID() + "-" + UUID.randomUUID())
                .expiresAt(LocalDateTime.now().plusDays(appProperties.getJwt().getRefreshTokenDays()))
                .build();
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(
                jwtService.generateAccessToken(user),
                refreshToken.getToken(),
                appProperties.getJwt().getAccessTokenMinutes() * 60,
                userService.toProfile(user)
        );
    }
}
