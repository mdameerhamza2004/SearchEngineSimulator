package com.searchengine.simulator.controller;

import com.searchengine.simulator.model.SearchHistory;
import com.searchengine.simulator.model.dto.AnalyticsOverviewDto;
import com.searchengine.simulator.model.dto.QueryAnalyticsDto;
import com.searchengine.simulator.model.dto.SearchInspectionDto;
import com.searchengine.simulator.service.AnalyticsService;
import com.searchengine.simulator.service.SearchEngineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final SearchEngineService searchEngineService;

    public AnalyticsController(AnalyticsService analyticsService, SearchEngineService searchEngineService) {
        this.analyticsService = analyticsService;
        this.searchEngineService = searchEngineService;
    }

    @GetMapping("/overview")
    public ResponseEntity<AnalyticsOverviewDto> getOverview() {
        return ResponseEntity.ok(analyticsService.getOverview());
    }

    @GetMapping("/top-queries")
    public ResponseEntity<List<QueryAnalyticsDto>> getTopQueries() {
        return ResponseEntity.ok(analyticsService.getTopQueries());
    }

    @GetMapping("/history")
    public ResponseEntity<List<SearchHistory>> getHistory() {
        return ResponseEntity.ok(analyticsService.getRecentHistory());
    }

    @DeleteMapping("/history")
    public ResponseEntity<Void> clearHistory() {
        analyticsService.clearHistory();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/inspect")
    public ResponseEntity<SearchInspectionDto> inspectSearch(@RequestParam String q) {
        if (q == null || q.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(searchEngineService.inspectSearchPerformance(q));
    }
}
