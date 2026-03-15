package com.aurabloom.service;

import com.aurabloom.dto.CommunityCommentRequest;
import com.aurabloom.dto.CommunityCommentResponse;
import com.aurabloom.dto.CommunityPostRequest;
import com.aurabloom.dto.CommunityPostResponse;
import com.aurabloom.dto.ReactionRequest;
import com.aurabloom.dto.ReportRequest;
import com.aurabloom.entity.CommunityComment;
import com.aurabloom.entity.CommunityPost;
import com.aurabloom.entity.PostModerationStatus;
import com.aurabloom.entity.PostReaction;
import com.aurabloom.entity.PostReport;
import com.aurabloom.entity.ReactionType;
import com.aurabloom.entity.UserAccount;
import com.aurabloom.exception.ApiException;
import com.aurabloom.repository.CommunityCommentRepository;
import com.aurabloom.repository.CommunityPostRepository;
import com.aurabloom.repository.PostReactionRepository;
import com.aurabloom.repository.PostReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityService {

    private final CommunityPostRepository communityPostRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final PostReactionRepository postReactionRepository;
    private final PostReportRepository postReportRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<CommunityPostResponse> listFeed() {
        return communityPostRepository.findByModerationStatusInOrderByCreatedAtDesc(List.of(PostModerationStatus.ACTIVE, PostModerationStatus.REPORTED)).stream()
                .map(this::toResponse)
                .toList();
    }

    public CommunityPostResponse createPost(String email, CommunityPostRequest request) {
        UserAccount user = userService.getRequiredUser(email);
        CommunityPost post = communityPostRepository.save(CommunityPost.builder()
                .user(user)
                .body(request.body())
                .moderationStatus(PostModerationStatus.ACTIVE)
                .reportCount(0)
                .build());
        return toResponse(post);
    }

    public CommunityPostResponse addComment(String email, Long postId, CommunityCommentRequest request) {
        UserAccount user = userService.getRequiredUser(email);
        CommunityPost post = findVisiblePost(postId);
        communityCommentRepository.save(CommunityComment.builder()
                .post(post)
                .user(user)
                .body(request.body())
                .build());
        return toResponse(post);
    }

    public CommunityPostResponse react(String email, Long postId, ReactionRequest request) {
        UserAccount user = userService.getRequiredUser(email);
        CommunityPost post = findVisiblePost(postId);
        PostReaction reaction = postReactionRepository.findByPostAndUser(post, user)
                .orElse(PostReaction.builder().post(post).user(user).build());
        reaction.setReactionType(request.reactionType());
        postReactionRepository.save(reaction);
        return toResponse(post);
    }

    public CommunityPostResponse report(String email, Long postId, ReportRequest request) {
        UserAccount user = userService.getRequiredUser(email);
        CommunityPost post = findVisiblePost(postId);
        if (postReportRepository.findByPostAndUser(post, user).isEmpty()) {
            postReportRepository.save(PostReport.builder()
                    .post(post)
                    .user(user)
                    .reason(request.reason())
                    .resolved(false)
                    .build());
            post.setReportCount(post.getReportCount() + 1);
            post.setModerationStatus(PostModerationStatus.REPORTED);
            communityPostRepository.save(post);
        }
        return toResponse(post);
    }

    @Transactional(readOnly = true)
    public List<CommunityPostResponse> reportedPosts() {
        return communityPostRepository.findByModerationStatusInOrderByCreatedAtDesc(List.of(PostModerationStatus.REPORTED)).stream()
                .map(this::toResponse)
                .toList();
    }

    public CommunityPostResponse hidePost(Long postId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Post not found"));
        post.setModerationStatus(PostModerationStatus.HIDDEN);
        return toResponse(communityPostRepository.save(post));
    }

    public CommunityPostResponse unhidePost(Long postId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Post not found"));
        post.setModerationStatus(PostModerationStatus.ACTIVE);
        return toResponse(communityPostRepository.save(post));
    }

    private CommunityPost findVisiblePost(Long postId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Post not found"));
        if (post.getModerationStatus() == PostModerationStatus.HIDDEN) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Post not found");
        }
        return post;
    }

    private CommunityPostResponse toResponse(CommunityPost post) {
        List<CommunityCommentResponse> comments = communityCommentRepository.findByPostOrderByCreatedAtAsc(post).stream()
                .map(comment -> new CommunityCommentResponse(
                        comment.getId(),
                        comment.getBody(),
                        comment.getCreatedAt(),
                        anonymousName(comment.getUser())
                ))
                .toList();

        return new CommunityPostResponse(
                post.getId(),
                post.getBody(),
                post.getCreatedAt(),
                post.getReportCount(),
                post.getModerationStatus(),
                postReactionRepository.countByPostAndReactionType(post, ReactionType.SUPPORT),
                postReactionRepository.countByPostAndReactionType(post, ReactionType.RELATE),
                anonymousName(post.getUser()),
                comments
        );
    }

    private String anonymousName(UserAccount user) {
        return "Member " + String.format("%03d", Math.floorMod(user.getId().intValue() * 17, 1000));
    }
}
