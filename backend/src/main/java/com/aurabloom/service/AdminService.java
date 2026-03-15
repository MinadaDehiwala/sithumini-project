package com.aurabloom.service;

import com.aurabloom.dto.AdminOverviewResponse;
import com.aurabloom.dto.HighRiskFlagResponse;
import com.aurabloom.dto.RecommendationTemplateRequest;
import com.aurabloom.dto.RecommendationTemplateResponse;
import com.aurabloom.entity.HighRiskFlag;
import com.aurabloom.entity.WellnessRecommendationTemplate;
import com.aurabloom.exception.ApiException;
import com.aurabloom.repository.CommunityPostRepository;
import com.aurabloom.repository.HighRiskFlagRepository;
import com.aurabloom.repository.PostReportRepository;
import com.aurabloom.repository.UserAccountRepository;
import com.aurabloom.repository.UserChallengeRepository;
import com.aurabloom.repository.WellnessRecommendationTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserAccountRepository userAccountRepository;
    private final CommunityPostRepository communityPostRepository;
    private final PostReportRepository postReportRepository;
    private final HighRiskFlagRepository highRiskFlagRepository;
    private final UserChallengeRepository userChallengeRepository;
    private final WellnessRecommendationTemplateRepository recommendationTemplateRepository;

    @Transactional(readOnly = true)
    public AdminOverviewResponse overview() {
        return new AdminOverviewResponse(
                userAccountRepository.count(),
                userAccountRepository.countByActiveTrue(),
                postReportRepository.countByResolvedFalse(),
                highRiskFlagRepository.countByResolvedFalse(),
                communityPostRepository.count(),
                userChallengeRepository.count()
        );
    }

    @Transactional(readOnly = true)
    public List<HighRiskFlagResponse> flags() {
        return highRiskFlagRepository.findByResolvedFalseOrderByCreatedAtDesc().stream()
                .map(this::toFlag)
                .toList();
    }

    public void resolveFlag(Long id) {
        HighRiskFlag flag = highRiskFlagRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Risk flag not found"));
        flag.setResolved(true);
        flag.setResolvedAt(LocalDateTime.now());
        highRiskFlagRepository.save(flag);
    }

    @Transactional(readOnly = true)
    public List<RecommendationTemplateResponse> listRecommendations() {
        return recommendationTemplateRepository.findAll().stream().map(this::toTemplate).toList();
    }

    public RecommendationTemplateResponse createRecommendation(RecommendationTemplateRequest request) {
        WellnessRecommendationTemplate template = recommendationTemplateRepository.save(WellnessRecommendationTemplate.builder()
                .title(request.title())
                .description(request.description())
                .category(request.category())
                .targetRiskLevel(request.targetRiskLevel())
                .active(request.active())
                .build());
        return toTemplate(template);
    }

    public RecommendationTemplateResponse updateRecommendation(Long id, RecommendationTemplateRequest request) {
        WellnessRecommendationTemplate template = recommendationTemplateRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Recommendation template not found"));
        template.setTitle(request.title());
        template.setDescription(request.description());
        template.setCategory(request.category());
        template.setTargetRiskLevel(request.targetRiskLevel());
        template.setActive(request.active());
        return toTemplate(recommendationTemplateRepository.save(template));
    }

    private HighRiskFlagResponse toFlag(HighRiskFlag flag) {
        return new HighRiskFlagResponse(
                flag.getId(),
                flag.getUser().getId(),
                flag.getUser().getFullName(),
                flag.getUser().getEmail(),
                flag.getRiskLevel(),
                flag.getScore(),
                flag.getSummary(),
                flag.isResolved(),
                flag.getCreatedAt()
        );
    }

    private RecommendationTemplateResponse toTemplate(WellnessRecommendationTemplate template) {
        return new RecommendationTemplateResponse(
                template.getId(),
                template.getTitle(),
                template.getDescription(),
                template.getCategory(),
                template.getTargetRiskLevel(),
                template.isActive()
        );
    }
}
