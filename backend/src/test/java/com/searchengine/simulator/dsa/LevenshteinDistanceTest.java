package com.searchengine.simulator.dsa;

import com.searchengine.simulator.dsa.spellcheck.LevenshteinDistance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LevenshteinDistanceTest {

    private LevenshteinDistance levenshtein;

    @BeforeEach
    void setUp() {
        levenshtein = new LevenshteinDistance();
    }

    @Test
    @DisplayName("Identical strings should have distance 0")
    void testIdenticalStrings() {
        assertEquals(0, levenshtein.calculate("test", "test"));
    }

    @Test
    @DisplayName("Single insertion should have distance 1")
    void testSingleInsertion() {
        assertEquals(1, levenshtein.calculate("test", "tests"));
    }

    @Test
    @DisplayName("Single deletion should have distance 1")
    void testSingleDeletion() {
        assertEquals(1, levenshtein.calculate("tests", "test"));
    }

    @Test
    @DisplayName("Single substitution should have distance 1")
    void testSingleSubstitution() {
        assertEquals(1, levenshtein.calculate("test", "tent"));
    }

    @Test
    @DisplayName("Classic kitten -> sitting example")
    void testKittenToSitting() {
        // kitten -> sitten (substitute k with s)
        // sitten -> sittin (substitute e with i)
        // sittin -> sitting (insert g)
        assertEquals(3, levenshtein.calculate("kitten", "sitting"));
    }

    @Test
    @DisplayName("Empty strings testing")
    void testEmptyStrings() {
        assertEquals(4, levenshtein.calculate("", "test"));
        assertEquals(4, levenshtein.calculate("test", ""));
        assertEquals(0, levenshtein.calculate("", ""));
        assertEquals(4, levenshtein.calculate(null, "test"));
    }

    @Test
    @DisplayName("Completely different strings")
    void testCompletelyDifferentStrings() {
        assertEquals(3, levenshtein.calculate("abc", "xyz"));
    }

    @Test
    @DisplayName("Test getOperations returns correct operation sequence")
    void testGetOperations() {
        List<String> ops = levenshtein.getOperations("cat", "bat");
        
        // Output should be roughly: SUBSTITUTE: c -> b, MATCH: a, MATCH: t
        assertEquals(3, ops.size());
        assertTrue(ops.get(0).startsWith("SUBSTITUTE"));
        assertEquals("MATCH: a", ops.get(1));
        assertEquals("MATCH: t", ops.get(2));
    }
}
