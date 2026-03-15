package com.aurabloom.repository;

import com.aurabloom.entity.CommunityPost;
import com.aurabloom.entity.PostReport;
import com.aurabloom.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    Optional<PostReport> findByPostAndUser(CommunityPost post, UserAccount user);
    List<PostReport> findByResolvedFalseOrderByCreatedAtAsc();
    long countByResolvedFalse();
}
