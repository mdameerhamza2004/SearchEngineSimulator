package com.searchengine.simulator.model.dto;

import java.util.List;
import java.util.Map;

public record DocumentStatsDto(
        Long id,
        int tokenCount,
        int uniqueTermCount,
        List<Map.Entry<String, Integer>> mostFrequentTerms
) {}
