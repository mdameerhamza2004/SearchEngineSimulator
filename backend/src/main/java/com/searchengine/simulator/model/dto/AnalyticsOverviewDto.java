package com.searchengine.simulator.model.dto;

public record AnalyticsOverviewDto(
        int totalDocuments,
        int totalSearchableTerms,
        long totalSearches,
        double averageLatency,
        double averageResults,
        String mostCommonQuery,
        long zeroResultSearches
) {}
