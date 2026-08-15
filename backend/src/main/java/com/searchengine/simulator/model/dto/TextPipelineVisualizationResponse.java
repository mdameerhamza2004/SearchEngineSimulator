package com.searchengine.simulator.model.dto;

import java.util.List;

/**
 * Response DTO for Text Processing Pipeline visualization endpoint.
 * Shows tokens at each stage of the pipeline.
 */
public record TextPipelineVisualizationResponse(
        String originalText,
        List<String> afterTokenization,     // split + lowercase
        List<String> afterStopWordRemoval,  // filtered
        List<String> afterStemming,         // fully processed
        List<String> removedStopWords,      // words that were removed
        List<StemChange> stemChanges        // which tokens changed during stemming
) {
    public record StemChange(
            String original,
            String stemmed,
            boolean changed
    ) {}
}
