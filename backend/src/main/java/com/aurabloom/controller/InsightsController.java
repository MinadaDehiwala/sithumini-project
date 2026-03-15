package com.aurabloom.controller;

import com.aurabloom.dto.InsightSummaryResponse;
import com.aurabloom.service.InsightsService;
import com.aurabloom.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/insights")
@RequiredArgsConstructor
public class InsightsController {

    private final InsightsService insightsService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<InsightSummaryResponse>> summary(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Insights loaded", insightsService.summary(authentication.getName())));
    }
}
