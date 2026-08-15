package com.searchengine.simulator.controller;

import com.searchengine.simulator.model.dto.*;
import com.searchengine.simulator.service.VisualizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Read-only controller exposing internal DSA algorithm data for frontend visualizations.
 * All endpoints are GET-only and do not mutate any state.
 */
@RestController
@RequestMapping("/api/visualization")
public class VisualizationController {

    private static final Logger log = LoggerFactory.getLogger(VisualizationController.class);
    private final VisualizationService visualizationService;

    public VisualizationController(VisualizationService visualizationService) {
        this.visualizationService = visualizationService;
    }

    /**
     * Returns Trie tree structure, traversal path, and suggestions for a given prefix.
     * GET /api/visualization/trie?prefix=pro
     */
    @GetMapping("/trie")
    public ResponseEntity<TrieVisualizationResponse> getTrieVisualization(
            @RequestParam(defaultValue = "") String prefix) {
        log.info("Visualization: Trie for prefix='{}'", prefix);
        return ResponseEntity.ok(visualizationService.getTrieVisualization(prefix));
    }

    /**
     * Returns real inverted index postings for a term.
     * GET /api/visualization/inverted-index?term=algorithm
     */
    @GetMapping("/inverted-index")
    public ResponseEntity<InvertedIndexVisualizationResponse> getInvertedIndexVisualization(
            @RequestParam String term) {
        log.info("Visualization: InvertedIndex for term='{}'", term);
        return ResponseEntity.ok(visualizationService.getInvertedIndexVisualization(term));
    }

    /**
     * Returns full TF-IDF breakdown for a query.
     * GET /api/visualization/tfidf?q=binary+search
     */
    @GetMapping("/tfidf")
    public ResponseEntity<TfIdfVisualizationResponse> getTfIdfVisualization(
            @RequestParam(name = "q") String query) {
        log.info("Visualization: TF-IDF for query='{}'", query);
        return ResponseEntity.ok(visualizationService.getTfIdfVisualization(query));
    }

    /**
     * Returns Levenshtein DP matrix and operations for two words.
     * GET /api/visualization/levenshtein?source=algoritm&target=algorithm
     */
    @GetMapping("/levenshtein")
    public ResponseEntity<LevenshteinVisualizationResponse> getLevenshteinVisualization(
            @RequestParam String source,
            @RequestParam String target) {
        log.info("Visualization: Levenshtein '{}' → '{}'", source, target);
        return ResponseEntity.ok(visualizationService.getLevenshteinVisualization(source, target));
    }

    /**
     * Returns text processing pipeline stages for input text.
     * GET /api/visualization/text-processing?text=The+algorithms+are+searching
     */
    @GetMapping("/text-processing")
    public ResponseEntity<TextPipelineVisualizationResponse> getTextPipelineVisualization(
            @RequestParam String text) {
        log.info("Visualization: TextPipeline for text='{}'", text);
        return ResponseEntity.ok(visualizationService.getTextPipelineVisualization(text));
    }
}
