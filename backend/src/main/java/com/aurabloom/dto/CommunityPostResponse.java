package com.aurabloom.dto;

import com.aurabloom.entity.PostModerationStatus;

import java.time.LocalDateTime;
import java.util.List;

public record CommunityPostResponse(
        Long id,
        String body,
        LocalDateTime createdAt,
        int reportCount,
        PostModerationStatus moderationStatus,
        long supportCount,
        long relateCount,
        String anonymousAuthor,
        List<CommunityCommentResponse> comments
) {
}
