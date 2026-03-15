package com.aurabloom.controller;

import com.aurabloom.dto.AdminOverviewResponse;
import com.aurabloom.dto.CommunityPostResponse;
import com.aurabloom.dto.HighRiskFlagResponse;
import com.aurabloom.dto.RecommendationTemplateRequest;
import com.aurabloom.dto.RecommendationTemplateResponse;
import com.aurabloom.service.AdminService;
import com.aurabloom.service.CommunityService;
import com.aurabloom.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final CommunityService communityService;

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<AdminOverviewResponse>> overview() {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Admin overview loaded", adminService.overview()));
    }

    @GetMapping("/risk-flags")
    public ResponseEntity<ApiResponse<List<HighRiskFlagResponse>>> flags() {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Risk flags loaded", adminService.flags()));
    }

    @PostMapping("/risk-flags/{id}/resolve")
    public ResponseEntity<ApiResponse<Void>> resolve(@PathVariable Long id) {
        adminService.resolveFlag(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Risk flag resolved", null));
    }

    @GetMapping("/reported-posts")
    public ResponseEntity<ApiResponse<List<CommunityPostResponse>>> reportedPosts() {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Reported posts loaded", communityService.reportedPosts()));
    }

    @PostMapping("/posts/{id}/hide")
    public ResponseEntity<ApiResponse<CommunityPostResponse>> hide(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Post hidden", communityService.hidePost(id)));
    }

    @PostMapping("/posts/{id}/unhide")
    public ResponseEntity<ApiResponse<CommunityPostResponse>> unhide(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Post restored", communityService.unhidePost(id)));
    }

    @GetMapping("/recommendations")
    public ResponseEntity<ApiResponse<List<RecommendationTemplateResponse>>> listRecommendations() {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Recommendation templates loaded", adminService.listRecommendations()));
    }

    @PostMapping("/recommendations")
    public ResponseEntity<ApiResponse<RecommendationTemplateResponse>> createRecommendation(@Valid @RequestBody RecommendationTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Recommendation template created", adminService.createRecommendation(request)));
    }

    @PutMapping("/recommendations/{id}")
    public ResponseEntity<ApiResponse<RecommendationTemplateResponse>> updateRecommendation(@PathVariable Long id,
                                                                                           @Valid @RequestBody RecommendationTemplateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Recommendation template updated", adminService.updateRecommendation(id, request)));
    }
}
