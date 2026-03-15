package com.aurabloom.repository;

import com.aurabloom.entity.CommunityComment;
import com.aurabloom.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {
    List<CommunityComment> findByPostOrderByCreatedAtAsc(CommunityPost post);
}
