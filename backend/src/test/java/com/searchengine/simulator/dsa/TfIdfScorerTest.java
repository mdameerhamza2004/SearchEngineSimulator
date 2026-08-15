package com.searchengine.simulator.dsa;

import com.searchengine.simulator.dsa.index.Posting;
import com.searchengine.simulator.dsa.ranking.TfIdfScorer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TfIdfScorerTest {

    private TfIdfScorer scorer;

    @BeforeEach
    void setUp() {
        scorer = new TfIdfScorer();
    }

    @Test
    @DisplayName("Test TF calculation with normal values")
    void testCalculateTf() {
        double tf = scorer.calculateTf(3, 100);
        assertEquals(0.03, tf, 0.0001);
    }

    @Test
    @DisplayName("Test TF calculation with zero total terms")
    void testCalculateTfZeroTotalTerms() {
        double tf = scorer.calculateTf(0, 0);
        assertEquals(0.0, tf, 0.0001);
    }

    @Test
    @DisplayName("Test IDF calculation with normal values")
    void testCalculateIdf() {
        // N = 1000, df = 10 -> IDF = ln(100) = 4.605
        double idf = scorer.calculateIdf(1000, 10);
        assertEquals(Math.log(100.0), idf, 0.0001);
    }

    @Test
    @DisplayName("Test IDF calculation when df is 0")
    void testCalculateIdfZeroDf() {
        double idf = scorer.calculateIdf(1000, 0);
        assertEquals(0.0, idf, 0.0001);
    }

    @Test
    @DisplayName("Test combined TF-IDF calculation")
    void testCalculateTfIdf() {
        double tfIdf = scorer.calculateTfIdf(3, 100, 1000, 10);
        double expected = (3.0 / 100.0) * Math.log(1000.0 / 10.0);
        assertEquals(expected, tfIdf, 0.0001);
    }

    @Test
    @DisplayName("Test scoreDocuments with multiple terms and documents")
    void testScoreDocuments() {
        Map<String, List<Posting>> termPostings = new HashMap<>();
        termPostings.put("apple", List.of(new Posting(1L, 2), new Posting(2L, 1)));
        termPostings.put("banana", List.of(new Posting(2L, 3)));

        Map<Long, Integer> docLengths = Map.of(1L, 100, 2L, 200);
        int totalDocuments = 10;

        Map<Long, Double> scores = scorer.scoreDocuments(
                List.of("apple", "banana"), termPostings, docLengths, totalDocuments);

        assertTrue(scores.containsKey(1L));
        assertTrue(scores.containsKey(2L));
        
        // Document 1 score: tf(apple)*idf(apple) = (2/100) * ln(10/2)
        double expectedDoc1 = (2.0 / 100.0) * Math.log(10.0 / 2.0);
        assertEquals(expectedDoc1, scores.get(1L), 0.0001);
        
        // Document 2 score: apple + banana
        double expectedDoc2 = ((1.0 / 200.0) * Math.log(10.0 / 2.0)) + 
                              ((3.0 / 200.0) * Math.log(10.0 / 1.0));
        assertEquals(expectedDoc2, scores.get(2L), 0.0001);
    }

    @Test
    @DisplayName("Test scoreDocuments with terms not in any doc")
    void testScoreDocumentsTermNotFound() {
        Map<String, List<Posting>> termPostings = new HashMap<>();
        termPostings.put("apple", List.of()); // empty postings
        Map<Long, Integer> docLengths = Map.of(1L, 100);

        Map<Long, Double> scores = scorer.scoreDocuments(
                List.of("apple", "banana"), termPostings, docLengths, 10);

        assertTrue(scores.isEmpty());
    }

    @Test
    @DisplayName("Test getScoreBreakdown returns per-term details")
    void testGetScoreBreakdown() {
        Map<String, List<Posting>> termPostings = new HashMap<>();
        termPostings.put("apple", List.of(new Posting(1L, 2)));
        termPostings.put("banana", List.of(new Posting(1L, 3)));

        Map<Long, Integer> docLengths = Map.of(1L, 100);
        
        Map<Long, Map<String, Double>> breakdown = scorer.getScoreBreakdown(
                List.of("apple", "banana"), termPostings, docLengths, 10);

        assertNotNull(breakdown.get(1L));
        assertTrue(breakdown.get(1L).containsKey("apple"));
        assertTrue(breakdown.get(1L).containsKey("banana"));
        
        double expectedApple = (2.0 / 100.0) * Math.log(10.0 / 1.0);
        double expectedBanana = (3.0 / 100.0) * Math.log(10.0 / 1.0);
        
        assertEquals(expectedApple, breakdown.get(1L).get("apple"), 0.0001);
        assertEquals(expectedBanana, breakdown.get(1L).get("banana"), 0.0001);
    }

    @Test
    @DisplayName("Test single doc corpus where all docs contain term")
    void testSingleDocCorpus() {
        Map<String, List<Posting>> termPostings = new HashMap<>();
        termPostings.put("apple", List.of(new Posting(1L, 1)));
        
        Map<Long, Double> scores = scorer.scoreDocuments(
                List.of("apple"), termPostings, Map.of(1L, 10), 1);
                
        // idf = ln(1/1) = 0, so score = 0
        assertEquals(0.0, scores.get(1L), 0.0001);
    }
}
