package com.aurabloom.controller;

import com.aurabloom.dto.AuthResponse;
import com.aurabloom.dto.LoginRequest;
import com.aurabloom.dto.PasswordResetRequest;
import com.aurabloom.dto.RefreshTokenRequest;
import com.aurabloom.dto.RegisterRequest;
import com.aurabloom.dto.UserProfileResponse;
import com.aurabloom.service.AuthService;
import com.aurabloom.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserProfileResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Registered successfully", authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Login successful", authService.login(request)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Token refreshed", authService.refresh(request)));
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<ApiResponse<Void>> passwordReset(@Valid @RequestBody PasswordResetRequest request) {
        authService.requestPasswordReset(request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Password reset initiated", null));
    }
}
