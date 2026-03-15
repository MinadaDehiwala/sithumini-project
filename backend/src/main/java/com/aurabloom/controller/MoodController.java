package com.aurabloom.controller;

import com.aurabloom.dto.MoodEntryRequest;
import com.aurabloom.dto.MoodEntryResponse;
import com.aurabloom.dto.MoodTrendResponse;
import com.aurabloom.service.MoodService;
import com.aurabloom.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/moods")
@RequiredArgsConstructor
public class MoodController {

    private final MoodService moodService;

    @PostMapping
    public ResponseEntity<ApiResponse<MoodEntryResponse>> create(Authentication authentication,
                                                                 @Valid @RequestBody MoodEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Mood logged", moodService.create(authentication.getName(), request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MoodEntryResponse>>> list(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Mood history loaded", moodService.list(authentication.getName())));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MoodEntryResponse>> update(Authentication authentication,
                                                                 @PathVariable Long id,
                                                                 @Valid @RequestBody MoodEntryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Mood updated", moodService.update(authentication.getName(), id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(Authentication authentication, @PathVariable Long id) {
        moodService.delete(authentication.getName(), id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Mood deleted", null));
    }

    @GetMapping("/trend/weekly")
    public ResponseEntity<ApiResponse<MoodTrendResponse>> weekly(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Weekly trend loaded", moodService.weeklyTrend(authentication.getName())));
    }

    @GetMapping("/trend/monthly")
    public ResponseEntity<ApiResponse<List<MoodTrendResponse>>> monthly(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Monthly trend loaded", moodService.monthlyTrend(authentication.getName())));
    }
}
