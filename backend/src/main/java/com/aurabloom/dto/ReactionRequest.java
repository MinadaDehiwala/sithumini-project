package com.aurabloom.dto;

import com.aurabloom.entity.ReactionType;
import jakarta.validation.constraints.NotNull;

public record ReactionRequest(
        @NotNull ReactionType reactionType
) {
}
