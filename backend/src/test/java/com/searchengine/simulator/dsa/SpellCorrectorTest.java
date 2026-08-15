package com.searchengine.simulator.dsa;

import com.searchengine.simulator.dsa.spellcheck.SpellCorrector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class SpellCorrectorTest {

    private SpellCorrector corrector;

    @BeforeEach
    void setUp() {
        corrector = new SpellCorrector();
    }

    @Test
    @DisplayName("Build dictionary with known words")
    void testBuildDictionary() {
        corrector.buildDictionary(Set.of("apple", "banana"));
        assertTrue(corrector.isKnownWord("apple"));
        assertTrue(corrector.isKnownWord("BANANA")); // should be case-insensitive
        assertFalse(corrector.isKnownWord("cherry"));
    }

    @Test
    @DisplayName("Known word returns itself as suggestion")
    void testKnownWordReturnsNoCorrections() {
        corrector.addWord("machine");
        List<String> suggestions = corrector.suggest("machine", 5);
        assertEquals(1, suggestions.size());
        assertEquals("machine", suggestions.get(0));
    }

    @Test
    @DisplayName("Simple misspelling gives correct suggestion")
    void testSimpleMisspelling() {
        corrector.addWord("machine");
        List<String> suggestions = corrector.suggest("machne", 5); // 1 deletion
        assertEquals(1, suggestions.size());
        assertEquals("machine", suggestions.get(0));
    }

    @Test
    @DisplayName("Multiple suggestions sorted by distance")
    void testMultipleSuggestions() {
        corrector.buildDictionary(Set.of("cat", "bat", "rat", "cart"));
        // "ca" to "cat" (1), "cart" (2), "bat" (2), "rat" (2)
        List<String> suggestions = corrector.suggest("ca", 3);
        assertEquals("cat", suggestions.get(0)); // closest
    }

    @Test
    @DisplayName("Test maxDistance threshold")
    void testMaxDistanceThreshold() {
        corrector.addWord("elephant");
        corrector.setMaxDistance(2);
        
        // "elep" is distance 4 from "elephant", so it shouldn't be suggested
        assertTrue(corrector.suggest("elep", 5).isEmpty());
        
        // "elephan" is distance 1
        assertEquals(1, corrector.suggest("elephan", 5).size());
    }

    @Test
    @DisplayName("correctQuery returns best guess string")
    void testCorrectQuery() {
        corrector.buildDictionary(Set.of("the", "quick", "brown", "fox"));
        
        String query = corrector.correctQuery(List.of("th", "quik", "brown", "fux"));
        assertEquals("the quick brown fox", query);
    }

    @Test
    @DisplayName("Empty dictionary behavior")
    void testEmptyDictionary() {
        List<String> suggestions = corrector.suggest("hello", 5);
        assertTrue(suggestions.isEmpty());
        
        String corrected = corrector.correctQuery(List.of("hello"));
        assertEquals("hello", corrected); // returns original if no dict
    }

    @Test
    @DisplayName("checkQuery returns map of misspelled terms")
    void testCheckQuery() {
        corrector.buildDictionary(Set.of("hello", "world"));
        
        Map<String, List<String>> misspelled = corrector.checkQuery(List.of("helo", "world"));
        
        assertTrue(misspelled.containsKey("helo"));
        assertFalse(misspelled.containsKey("world"));
        
        List<String> heloSuggestions = misspelled.get("helo");
        assertEquals(1, heloSuggestions.size());
        assertEquals("hello", heloSuggestions.get(0));
    }
}
