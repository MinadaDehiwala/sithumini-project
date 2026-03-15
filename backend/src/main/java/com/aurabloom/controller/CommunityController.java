package com.aurabloom.controller;

import com.aurabloom.dto.CommunityCommentRequest;
import com.aurabloom.dto.CommunityPostRequest;
import com.aurabloom.dto.CommunityPostResponse;
import com.aurabloom.dto.ReactionRequest;
import com.aurabloom.dto.ReportRequest;
import com.aurabloom.service.CommunityService;
import com.aurabloom.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<List<CommunityPostResponse>>> feed() {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Community feed loaded", communityService.listFeed()));
    }

    @PostMapping("/posts")
    public ResponseEntity<ApiResponse<CommunityPostResponse>> create(Authentication authentication,
                                                                    @Valid @RequestBody CommunityPostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Post created", communityService.createPost(authentication.getName(), request)));
    }

    @PostMapping("/posts/{id}/comments")
    public ResponseEntity<ApiResponse<CommunityPostResponse>> comment(Authentication authentication,
                                                                     @PathVariable Long id,
                                                                     @Valid @RequestBody CommunityCommentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Comment added", communityService.addComment(authentication.getName(), id, request)));
    }

    @PostMapping("/posts/{id}/reactions")
    public ResponseEntity<ApiResponse<CommunityPostResponse>> react(Authentication authentication,
                                                                   @PathVariable Long id,
                                                                   @Valid @RequestBody ReactionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Reaction saved", communityService.react(authentication.getName(), id, request)));
    }

    @PostMapping("/posts/{id}/reports")
    public ResponseEntity<ApiResponse<CommunityPostResponse>> report(Authentication authentication,
                                                                    @PathVariable Long id,
                                                                    @Valid @RequestBody ReportRequest request) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Post reported", communityService.report(authentication.getName(), id, request)));
    }
}
