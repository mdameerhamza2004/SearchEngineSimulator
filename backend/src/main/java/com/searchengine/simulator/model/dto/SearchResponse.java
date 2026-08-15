package com.searchengine.simulator.model.dto;

import java.util.List;
import java.util.Map;

public record SearchResponse(
        String query,
        String correctedQuery,
        int totalResults,
        long executionTimeMs,
        List<SearchResultItem> results,
        Map<String, List<String>> spellingSuggestions
) {
}
