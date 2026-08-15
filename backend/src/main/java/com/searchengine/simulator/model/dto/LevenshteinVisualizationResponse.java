package com.searchengine.simulator.model.dto;

import java.util.List;

/**
 * Response DTO for Levenshtein Distance visualization endpoint.
 * Contains the full DP matrix and step-by-step operations for animation.
 */
public record LevenshteinVisualizationResponse(
        String source,
        String target,
        int editDistance,
        int[][] matrix,         // full (m+1) x (n+1) DP matrix
        List<String> operations // MATCH/INSERT/DELETE/SUBSTITUTE operations from backtrack
) {}
