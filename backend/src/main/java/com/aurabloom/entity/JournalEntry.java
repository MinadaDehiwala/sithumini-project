package com.aurabloom.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "journal_entries")
public class JournalEntry extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    private LocalDate entryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JournalSentiment sentiment;

    @Column(nullable = false)
    private int sentimentScore;

    @ElementCollection
    @CollectionTable(name = "journal_keywords", joinColumns = @JoinColumn(name = "journal_entry_id"))
    @Column(name = "keyword", nullable = false)
    @Builder.Default
    private List<String> keywords = new ArrayList<>();
}
