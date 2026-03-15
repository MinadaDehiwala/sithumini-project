package com.aurabloom.controller;

import com.aurabloom.dto.TimeCapsuleRequest;
import com.aurabloom.dto.TimeCapsuleResponse;
import com.aurabloom.service.TimeCapsuleService;
import com.aurabloom.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/time-capsules")
@RequiredArgsConstructor
public class TimeCapsuleController {

    private final TimeCapsuleService timeCapsuleService;

    @PostMapping
    public ResponseEntity<ApiResponse<TimeCapsuleResponse>> create(Authentication authentication,
                                                                   @Valid @RequestBody TimeCapsuleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Time capsule created", timeCapsuleService.create(authentication.getName(), request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TimeCapsuleResponse>>> list(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Time capsules loaded", timeCapsuleService.list(authentication.getName())));
    }
}
