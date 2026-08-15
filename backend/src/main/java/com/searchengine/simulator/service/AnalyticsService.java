package com.searchengine.simulator.service;

import com.searchengine.simulator.model.SearchHistory;
import com.searchengine.simulator.model.dto.AnalyticsOverviewDto;
import com.searchengine.simulator.model.dto.QueryAnalyticsDto;
import com.searchengine.simulator.repository.SearchHistoryRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final SearchEngineService searchEngineService;

    public AnalyticsService(SearchHistoryRepository searchHistoryRepository, SearchEngineService searchEngineService) {
        this.searchHistoryRepository = searchHistoryRepository;
        this.searchEngineService = searchEngineService;
    }

    @Async
    @Transactional
    public void logSearch(String query, int resultCount, long executionTimeMs) {
        if (query == null || query.isBlank()) return;
        SearchHistory history = new SearchHistory(query, resultCount, executionTimeMs);
        searchHistoryRepository.save(history);
    }

    @Transactional(readOnly = true)
    public AnalyticsOverviewDto getOverview() {
        long totalSearches = searchHistoryRepository.count();
        int totalDocuments = searchEngineService.getIndexedDocumentCount();
        int totalSearchableTerms = searchEngineService.getInvertedIndex().getAllTerms().size();
        
        Double avgLatency = searchHistoryRepository.getAverageLatency();
        Double avgResults = searchHistoryRepository.getAverageResults();
        long zeroResults = searchHistoryRepository.countZeroResultSearches();
        
        List<Object[]> topQueries = searchHistoryRepository.findTopQueries();
        String mostCommonQuery = topQueries.isEmpty() ? "None" : (String) topQueries.get(0)[0];

        return new AnalyticsOverviewDto(
                totalDocuments,
                totalSearchableTerms,
                totalSearches,
                avgLatency != null ? avgLatency : 0.0,
                avgResults != null ? avgResults : 0.0,
                mostCommonQuery,
                zeroResults
        );
    }

    @Transactional(readOnly = true)
    public List<QueryAnalyticsDto> getTopQueries() {
        return searchHistoryRepository.findTopQueries().stream()
                .map(row -> new QueryAnalyticsDto(
                        (String) row[0],
                        ((Number) row[1]).longValue(),
                        ((Number) row[2]).doubleValue(),
                        ((Number) row[3]).doubleValue()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SearchHistory> getRecentHistory() {
        return searchHistoryRepository.findTop20ByOrderByTimestampDesc();
    }
    
    @Transactional
    public void clearHistory() {
        searchHistoryRepository.deleteAll();
    }
}
