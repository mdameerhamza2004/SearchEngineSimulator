package com.searchengine.simulator.model.dto;

import java.util.List;

/**
 * Response DTO for Inverted Index visualization endpoint.
 * Shows real posting data for a searched term.
 */
public record InvertedIndexVisualizationResponse(
        String term,            // the searched term (as stored in index, i.e. stemmed)
        String originalTerm,    // the user-input term before processing
        int documentFrequency,
        int totalDocuments,
        List<PostingDto> postings
) {
    public record PostingDto(
            long docId,
            String title,
            String category,
            int termFrequency,
            int documentLength,
            double tf,          // pre-calculated TF for display
            List<Integer> positions
    ) {}
}
