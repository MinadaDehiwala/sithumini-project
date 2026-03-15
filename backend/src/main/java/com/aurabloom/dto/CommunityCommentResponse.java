package com.aurabloom.dto;

import java.time.LocalDateTime;

public record CommunityCommentResponse(
        Long id,
        String body,
        LocalDateTime createdAt,
        String anonymousAuthor
) {
}
