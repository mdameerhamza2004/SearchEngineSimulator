package com.searchengine.simulator.dsa;

import com.searchengine.simulator.dsa.ranking.SearchResultRanker;
import com.searchengine.simulator.dsa.ranking.SearchResultRanker.ScoredDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SearchResultRankerTest {

    private SearchResultRanker ranker;

    @BeforeEach
    void setUp() {
        ranker = new SearchResultRanker();
    }

    @Test
    @DisplayName("Test rankTopK returns correct descending order")
    void testRankTopKCorrectOrder() {
        Map<Long, Double> scores = Map.of(
            1L, 1.5,
            2L, 4.0,
            3L, 2.0,
            4L, 5.5,
            5L, 0.5
        );
        
        List<ScoredDocument> result = ranker.rankTopK(scores, 3);
        
        assertEquals(3, result.size());
        assertEquals(4L, result.get(0).docId());
        assertEquals(5.5, result.get(0).score(), 0.0001);
        assertEquals(2L, result.get(1).docId());
        assertEquals(3L, result.get(2).docId());
    }

    @Test
    @DisplayName("Test rankTopK with K larger than total results")
    void testRankTopKLargerThanTotal() {
        Map<Long, Double> scores = Map.of(1L, 1.0, 2L, 2.0);
        List<ScoredDocument> result = ranker.rankTopK(scores, 10);
        
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).docId());
        assertEquals(1L, result.get(1).docId());
    }

    @Test
    @DisplayName("Test rankTopK with K = 1")
    void testRankTopKSingleResult() {
        Map<Long, Double> scores = Map.of(1L, 1.0, 2L, 3.5, 3L, 2.0);
        List<ScoredDocument> result = ranker.rankTopK(scores, 1);
        
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).docId());
    }

    @Test
    @DisplayName("Test rankTopK with empty scores or invalid K")
    void testRankTopKEmptyOrInvalidK() {
        assertTrue(ranker.rankTopK(new HashMap<>(), 5).isEmpty());
        assertTrue(ranker.rankTopK(Map.of(1L, 1.0), 0).isEmpty());
        assertTrue(ranker.rankTopK(Map.of(1L, 1.0), -1).isEmpty());
        assertTrue(ranker.rankTopK(null, 5).isEmpty());
    }

    @Test
    @DisplayName("Test tie-breaking behavior for identical scores")
    void testTieBreaking() {
        Map<Long, Double> scores = Map.of(
            5L, 2.0,
            2L, 2.0,
            8L, 2.0
        );
        
        List<ScoredDocument> result = ranker.rankTopK(scores, 3);
        
        // Due to Comparable implementation, smaller docId should win ties
        assertEquals(2L, result.get(0).docId());
        assertEquals(5L, result.get(1).docId());
        assertEquals(8L, result.get(2).docId());
    }

    @Test
    @DisplayName("Test rankAll returns all sorted results")
    void testRankAll() {
        Map<Long, Double> scores = Map.of(
            1L, 1.0,
            2L, 3.0,
            3L, 2.0
        );
        
        List<ScoredDocument> result = ranker.rankAll(scores);
        
        assertEquals(3, result.size());
        assertEquals(2L, result.get(0).docId());
        assertEquals(3L, result.get(1).docId());
        assertEquals(1L, result.get(2).docId());
    }
}
