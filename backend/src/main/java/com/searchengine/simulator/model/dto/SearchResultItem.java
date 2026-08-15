package com.searchengine.simulator.model.dto;

import java.util.List;
import java.util.Map;

public record SearchResultItem(
        long docId,
        String title,
        String snippet,
        double score,
        List<String> matchedTerms,
        Map<String, Double> scoreBreakdown,
        String category
) {
}
