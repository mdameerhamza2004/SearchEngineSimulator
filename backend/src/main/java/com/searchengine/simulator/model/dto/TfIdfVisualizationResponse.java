package com.searchengine.simulator.model.dto;

import java.util.List;
import java.util.Map;

/**
 * Response DTO for TF-IDF visualization endpoint.
 * Exposes the full ranking pipeline data for a query.
 */
public record TfIdfVisualizationResponse(
        String query,
        List<String> rawTokens,
        List<String> processedTerms,   // after stop-word removal + stemming
        int totalDocuments,
        List<TermStats> termStats,     // per-term statistics
        List<DocumentScore> documentScores  // per-document aggregated scores
) {
    public record TermStats(
            String term,
            int documentFrequency,
            double idf
    ) {}

    public record DocumentScore(
            long docId,
            String title,
            String category,
            double finalScore,
            List<TermDocScore> termBreakdown  // per-term contribution for this doc
    ) {}

    public record TermDocScore(
            String term,
            int termFrequency,
            int documentLength,
            double tf,
            double idf,
            double tfIdf
    ) {}
}
