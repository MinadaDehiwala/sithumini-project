package com.aurabloom.controller;

import com.aurabloom.dto.JournalEntryRequest;
import com.aurabloom.dto.JournalEntryResponse;
import com.aurabloom.service.JournalService;
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
@RequestMapping("/api/journals")
@RequiredArgsConstructor
public class JournalController {

    private final JournalService journalService;

    @PostMapping
    public ResponseEntity<ApiResponse<JournalEntryResponse>> create(Authentication authentication,
                                                                    @Valid @RequestBody JournalEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Journal entry created", journalService.create(authentication.getName(), request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JournalEntryResponse>>> list(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Journal entries loaded", journalService.list(authentication.getName())));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JournalEntryResponse>> update(Authentication authentication,
                                                                    @PathVariable Long id,
                                                                    @Valid @RequestBody JournalEntryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Journal updated", journalService.update(authentication.getName(), id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(Authentication authentication, @PathVariable Long id) {
        journalService.delete(authentication.getName(), id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Journal entry deleted", null));
    }
}
