package com.searchengine.simulator.model.dto;

public record QueryAnalyticsDto(
        String query,
        long searchCount,
        double avgResults,
        double avgLatency
) {}
