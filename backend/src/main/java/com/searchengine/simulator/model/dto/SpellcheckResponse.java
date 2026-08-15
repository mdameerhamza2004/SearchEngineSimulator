package com.searchengine.simulator.model.dto;

import java.util.List;
import java.util.Map;

public record SpellcheckResponse(
        String query,
        String correctedQuery,
        Map<String, List<String>> corrections,
        long executionTimeMs
) {
}
