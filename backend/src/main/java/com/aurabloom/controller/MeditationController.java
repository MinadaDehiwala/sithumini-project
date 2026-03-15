package com.aurabloom.controller;

import com.aurabloom.dto.MeditationSessionRequest;
import com.aurabloom.dto.MeditationSessionResponse;
import com.aurabloom.dto.MeditationStatsResponse;
import com.aurabloom.service.MeditationService;
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
@RequestMapping("/api/meditations")
@RequiredArgsConstructor
public class MeditationController {

    private final MeditationService meditationService;

    @PostMapping
    public ResponseEntity<ApiResponse<MeditationSessionResponse>> create(Authentication authentication,
                                                                         @Valid @RequestBody MeditationSessionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Meditation session logged", meditationService.create(authentication.getName(), request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MeditationSessionResponse>>> list(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Meditation sessions loaded", meditationService.list(authentication.getName())));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<MeditationStatsResponse>> stats(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Meditation stats loaded", meditationService.stats(authentication.getName())));
    }
}
