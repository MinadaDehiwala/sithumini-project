package com.aurabloom.service;

import com.aurabloom.entity.JournalSentiment;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SentimentAnalysisService {

    private static final Set<String> POSITIVE_WORDS = Set.of(
            "calm", "grateful", "hopeful", "joy", "rested", "happy", "steady", "relieved", "proud", "peaceful"
    );

    private static final Set<String> NEGATIVE_WORDS = Set.of(
            "sad", "stress", "stressed", "anxious", "panic", "overwhelmed", "tired", "lonely", "afraid", "angry"
    );

    private static final Set<String> STOP_WORDS = Set.of(
            "the", "and", "that", "with", "have", "this", "from", "about", "into", "your", "today", "really"
    );

    public AnalysisResult analyze(String title, String body) {
        List<String> words = Arrays.stream((title + " " + body).toLowerCase().split("[^a-z]+"))
                .filter(word -> !word.isBlank())
                .toList();

        int score = 0;
        for (String word : words) {
            if (POSITIVE_WORDS.contains(word)) {
                score++;
            }
            if (NEGATIVE_WORDS.contains(word)) {
                score--;
            }
        }

        JournalSentiment sentiment = score > 1 ? JournalSentiment.POSITIVE : score < 0 ? JournalSentiment.NEGATIVE : JournalSentiment.NEUTRAL;

        List<String> keywords = words.stream()
                .filter(word -> word.length() > 3)
                .filter(word -> !STOP_WORDS.contains(word))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();

        return new AnalysisResult(sentiment, score, keywords);
    }

    public record AnalysisResult(JournalSentiment sentiment, int score, List<String> keywords) {
    }
}
