package com.aurabloom.service;

import com.aurabloom.dto.MoodEntryRequest;
import com.aurabloom.dto.MoodEntryResponse;
import com.aurabloom.dto.MoodTrendResponse;
import com.aurabloom.entity.MoodEntry;
import com.aurabloom.entity.MoodType;
import com.aurabloom.entity.UserAccount;
import com.aurabloom.exception.ApiException;
import com.aurabloom.repository.MoodEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class MoodService {

    private final MoodEntryRepository moodEntryRepository;
    private final UserService userService;
    private final GamificationService gamificationService;

    public MoodEntryResponse create(String email, MoodEntryRequest request) {
        UserAccount user = userService.getRequiredUser(email);
        MoodEntry entry = moodEntryRepository.save(MoodEntry.builder()
                .user(user)
                .moodType(request.moodType())
                .note(request.note())
                .entryDate(request.entryDate() == null ? LocalDate.now() : request.entryDate())
                .build());
        gamificationService.awardExperience(user, 10);
        return toDto(entry);
    }

    @Transactional(readOnly = true)
    public List<MoodEntryResponse> list(String email) {
        UserAccount user = userService.getRequiredUser(email);
        return moodEntryRepository.findByUserOrderByEntryDateDescCreatedAtDesc(user).stream()
                .map(this::toDto)
                .toList();
    }

    public MoodEntryResponse update(String email, Long id, MoodEntryRequest request) {
        UserAccount user = userService.getRequiredUser(email);
        MoodEntry entry = findOwned(id, user);
        entry.setMoodType(request.moodType());
        entry.setNote(request.note());
        entry.setEntryDate(request.entryDate() == null ? entry.getEntryDate() : request.entryDate());
        return toDto(moodEntryRepository.save(entry));
    }

    public void delete(String email, Long id) {
        UserAccount user = userService.getRequiredUser(email);
        moodEntryRepository.delete(findOwned(id, user));
    }

    @Transactional(readOnly = true)
    public MoodTrendResponse weeklyTrend(String email) {
        UserAccount user = userService.getRequiredUser(email);
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(today.getDayOfWeek().getValue() - 1L);
        return buildTrend(user, start, start.plusDays(6));
    }

    @Transactional(readOnly = true)
    public List<MoodTrendResponse> monthlyTrend(String email) {
        UserAccount user = userService.getRequiredUser(email);
        LocalDate today = LocalDate.now();
        List<MoodTrendResponse> trends = new ArrayList<>();
        for (int month = 1; month <= today.getMonthValue(); month++) {
            LocalDate start = LocalDate.of(today.getYear(), month, 1);
            trends.add(buildTrend(user, start, start.withDayOfMonth(start.lengthOfMonth())));
        }
        return trends;
    }

    private MoodTrendResponse buildTrend(UserAccount user, LocalDate start, LocalDate end) {
        Map<MoodType, Long> counts = new EnumMap<>(MoodType.class);
        moodEntryRepository.findByUserAndEntryDateBetweenOrderByEntryDateAsc(user, start, end)
                .forEach(entry -> counts.merge(entry.getMoodType(), 1L, Long::sum));
        return new MoodTrendResponse(start, end, counts);
    }

    private MoodEntry findOwned(Long id, UserAccount user) {
        MoodEntry entry = moodEntryRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Mood entry not found"));
        if (!entry.getUser().getId().equals(user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "You do not have access to this mood entry");
        }
        return entry;
    }

    private MoodEntryResponse toDto(MoodEntry entry) {
        return new MoodEntryResponse(entry.getId(), entry.getMoodType(), entry.getNote(), entry.getEntryDate());
    }
}
