package com.aurabloom.controller;

import com.aurabloom.dto.ChallengeResponse;
import com.aurabloom.dto.ChallengeTemplateRequest;
import com.aurabloom.dto.ChallengeTemplateResponse;
import com.aurabloom.service.ChallengeService;
import com.aurabloom.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<ChallengeResponse>> today(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Today's challenge loaded", challengeService.getTodayChallenge(authentication.getName())));
    }

    @PostMapping("/today/complete")
    public ResponseEntity<ApiResponse<ChallengeResponse>> complete(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Challenge completed", challengeService.completeToday(authentication.getName())));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<ChallengeResponse>>> history(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Challenge history loaded", challengeService.history(authentication.getName())));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/templates")
    public ResponseEntity<ApiResponse<List<ChallengeTemplateResponse>>> templates() {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Challenge templates loaded", challengeService.listTemplates()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/templates")
    public ResponseEntity<ApiResponse<ChallengeTemplateResponse>> createTemplate(@Valid @RequestBody ChallengeTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Challenge template created", challengeService.createTemplate(request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/templates/{id}")
    public ResponseEntity<ApiResponse<ChallengeTemplateResponse>> updateTemplate(@PathVariable Long id,
                                                                                 @Valid @RequestBody ChallengeTemplateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Challenge template updated", challengeService.updateTemplate(id, request)));
    }
}
