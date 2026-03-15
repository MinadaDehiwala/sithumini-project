package com.aurabloom.repository;

import com.aurabloom.entity.CommunityPost;
import com.aurabloom.entity.PostModerationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    List<CommunityPost> findByModerationStatusInOrderByCreatedAtDesc(Collection<PostModerationStatus> statuses);
    long countByModerationStatus(PostModerationStatus status);
}
