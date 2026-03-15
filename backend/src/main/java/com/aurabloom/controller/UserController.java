package com.aurabloom.controller;

import com.aurabloom.dto.AdminUserResponse;
import com.aurabloom.dto.RoleUpdateRequest;
import com.aurabloom.dto.UpdateProfileRequest;
import com.aurabloom.dto.UserProfileResponse;
import com.aurabloom.service.UserService;
import com.aurabloom.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> profile(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Profile loaded", userService.getProfile(authentication.getName())));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(Authentication authentication,
                                                                          @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Profile updated", userService.updateProfile(authentication.getName(), request)));
    }

    @DeleteMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> deleteProfile(Authentication authentication) {
        userService.deleteProfile(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Profile deleted", null));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> users() {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Users loaded", userService.listUsers()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/role")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateRole(@PathVariable Long id,
                                                                     @Valid @RequestBody RoleUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "User updated", userService.updateUser(id, request)));
    }
}
