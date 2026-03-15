package com.aurabloom.repository;

import com.aurabloom.entity.CommunityPost;
import com.aurabloom.entity.PostReaction;
import com.aurabloom.entity.ReactionType;
import com.aurabloom.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {
    Optional<PostReaction> findByPostAndUser(CommunityPost post, UserAccount user);
    List<PostReaction> findByPost(CommunityPost post);
    long countByPostAndReactionType(CommunityPost post, ReactionType reactionType);
}
