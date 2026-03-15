package com.aurabloom.service;

import com.aurabloom.dto.JournalEntryRequest;
import com.aurabloom.dto.JournalEntryResponse;
import com.aurabloom.entity.JournalEntry;
import com.aurabloom.entity.UserAccount;
import com.aurabloom.exception.ApiException;
import com.aurabloom.repository.JournalEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JournalService {

    private final JournalEntryRepository journalEntryRepository;
    private final SentimentAnalysisService sentimentAnalysisService;
    private final UserService userService;
    private final GamificationService gamificationService;

    public JournalEntryResponse create(String email, JournalEntryRequest request) {
        UserAccount user = userService.getRequiredUser(email);
        SentimentAnalysisService.AnalysisResult analysis = sentimentAnalysisService.analyze(request.title(), request.body());
        JournalEntry entry = journalEntryRepository.save(JournalEntry.builder()
                .user(user)
                .title(request.title())
                .body(request.body())
                .entryDate(request.entryDate() == null ? LocalDate.now() : request.entryDate())
                .sentiment(analysis.sentiment())
                .sentimentScore(analysis.score())
                .keywords(analysis.keywords())
                .build());
        gamificationService.awardExperience(user, 15);
        return toDto(entry);
    }

    @Transactional(readOnly = true)
    public List<JournalEntryResponse> list(String email) {
        UserAccount user = userService.getRequiredUser(email);
        return journalEntryRepository.findByUserOrderByEntryDateDescCreatedAtDesc(user).stream()
                .map(this::toDto)
                .toList();
    }

    public JournalEntryResponse update(String email, Long id, JournalEntryRequest request) {
        UserAccount user = userService.getRequiredUser(email);
        JournalEntry entry = findOwned(id, user);
        SentimentAnalysisService.AnalysisResult analysis = sentimentAnalysisService.analyze(request.title(), request.body());
        entry.setTitle(request.title());
        entry.setBody(request.body());
        entry.setEntryDate(request.entryDate() == null ? entry.getEntryDate() : request.entryDate());
        entry.setSentiment(analysis.sentiment());
        entry.setSentimentScore(analysis.score());
        entry.setKeywords(analysis.keywords());
        return toDto(journalEntryRepository.save(entry));
    }

    public void delete(String email, Long id) {
        UserAccount user = userService.getRequiredUser(email);
        journalEntryRepository.delete(findOwned(id, user));
    }

    private JournalEntry findOwned(Long id, UserAccount user) {
        JournalEntry entry = journalEntryRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Journal entry not found"));
        if (!entry.getUser().getId().equals(user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You do not have access to this journal entry");
        }
        return entry;
    }

    private JournalEntryResponse toDto(JournalEntry entry) {
        return new JournalEntryResponse(
                entry.getId(),
                entry.getTitle(),
                entry.getBody(),
                entry.getEntryDate(),
                entry.getSentiment(),
                entry.getSentimentScore(),
                entry.getKeywords()
        );
    }
}
