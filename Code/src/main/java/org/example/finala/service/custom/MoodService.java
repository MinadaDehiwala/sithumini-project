package org.example.finala.service.custom;

import org.example.finala.dto.MoodEntryDTO;
import org.example.finala.dto.MoodTrendDTO;
import java.util.*;

public interface MoodService {
    public MoodEntryDTO createMood(String email, MoodEntryDTO dto) ;

    public List<MoodEntryDTO> getAllMoods(String email);

    public MoodEntryDTO updateMood(String email, Long id, MoodEntryDTO dto);

    public void deleteMood(String email, Long id) ;

    public List<MoodTrendDTO> getWeeklyTrend(String email);

    public List<MoodTrendDTO> getMonthlyTrend(String email);

    public double getEmotionalRiskScore(String email);
}
