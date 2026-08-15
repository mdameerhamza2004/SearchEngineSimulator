package com.searchengine.simulator.model.dto;

import java.util.List;

public record AutocompleteResponse(
        String prefix,
        List<String> suggestions,
        long executionTimeMs
) {
}
