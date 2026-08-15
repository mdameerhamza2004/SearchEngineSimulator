package com.searchengine.simulator.controller;

import com.searchengine.simulator.model.dto.AutocompleteResponse;
import com.searchengine.simulator.model.dto.SearchResponse;
import com.searchengine.simulator.model.dto.SpellcheckResponse;
import com.searchengine.simulator.service.AnalyticsService;
import com.searchengine.simulator.service.SearchEngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api")
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);
    private final SearchEngineService searchEngineService;
    private final AnalyticsService analyticsService;

    public SearchController(SearchEngineService searchEngineService, AnalyticsService analyticsService) {
        this.searchEngineService = searchEngineService;
        this.analyticsService = analyticsService;
    }

    /**
     * Executes a search query.
     *
     * @param q     The search query string.
     * @param limit The maximum number of results to return.
     * @return Search results and metadata.
     */
    @GetMapping("/search")
    public ResponseEntity<SearchResponse> search(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "10") int limit) {
        if (q == null || q.isBlank()) {
            return ResponseEntity.ok(new SearchResponse(
                    q == null ? "" : q,
                    q == null ? "" : q,
                    0,
                    0L,
                    Collections.emptyList(),
                    Collections.emptyMap()
            ));
        }
        log.info("Executing search for query: '{}' with limit: {}", q, limit);
        SearchResponse response = searchEngineService.search(q, limit);
        
        // Asynchronously log search history
        analyticsService.logSearch(response.query(), response.totalResults(), response.executionTimeMs());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Provides autocomplete suggestions for a given prefix.
     *
     * @param prefix The prefix to suggest completions for.
     * @param limit  The maximum number of suggestions to return.
     * @return Autocomplete suggestions.
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<AutocompleteResponse> autocomplete(
            @RequestParam(required = false) String prefix,
            @RequestParam(defaultValue = "10") int limit) {
        if (prefix == null || prefix.isBlank()) {
            return ResponseEntity.ok(new AutocompleteResponse(
                    prefix == null ? "" : prefix,
                    Collections.emptyList(),
                    0L
            ));
        }
        log.info("Executing autocomplete for prefix: '{}' with limit: {}", prefix, limit);
        AutocompleteResponse response = searchEngineService.autocomplete(prefix, limit);
        return ResponseEntity.ok(response);
    }

    /**
     * Checks spelling for a given query and provides suggestions.
     *
     * @param q The query to spellcheck.
     * @return Spellcheck results and suggestions.
     */
    @GetMapping("/spellcheck")
    public ResponseEntity<SpellcheckResponse> spellcheck(
            @RequestParam(required = false) String q) {
        if (q == null || q.isBlank()) {
            return ResponseEntity.ok(new SpellcheckResponse(
                    q == null ? "" : q,
                    q == null ? "" : q,
                    Collections.emptyMap(),
                    0L
            ));
        }
        log.info("Executing spellcheck for query: '{}'", q);
        SpellcheckResponse response = searchEngineService.spellcheck(q);
        return ResponseEntity.ok(response);
    }
}
