package com.searchengine.simulator.model.dto;

public record SearchInspectionDto(
        String query,
        double tokenizationMs,
        double indexLookupMs,
        double tfIdfScoringMs,
        double rankingMs,
        double totalMs,
        int totalResults
) {}
