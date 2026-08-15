package com.searchengine.simulator.dsa;

import com.searchengine.simulator.dsa.trie.Trie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TrieTest {

    private Trie trie;

    @BeforeEach
    void setUp() {
        trie = new Trie();
    }

    @Test
    @DisplayName("Should successfully insert words and allow exact search")
    void testInsertAndExactSearch() {
        trie.insert("hello");
        trie.insert("world");
        
        assertTrue(trie.search("hello"));
        assertTrue(trie.search("world"));
        assertFalse(trie.search("hell")); // prefix, not word
        assertFalse(trie.search("java")); // non-existent
    }

    @Test
    @DisplayName("Should accurately evaluate prefix checks")
    void testStartsWith() {
        trie.insert("programming");
        
        assertTrue(trie.startsWith("prog"));
        assertTrue(trie.startsWith("programming"));
        assertFalse(trie.startsWith("gram")); // substring, not prefix
        assertFalse(trie.startsWith("progs"));
    }

    @Test
    @DisplayName("Should return top-K suggestions ranked by frequency")
    void testGetSuggestionsTopK() {
        trie.insert("app", 5);
        trie.insert("apple", 10);
        trie.insert("application", 15);
        trie.insert("apply", 2);
        
        List<String> suggestions = trie.getSuggestions("app", 3);
        
        assertEquals(3, suggestions.size());
        assertEquals("application", suggestions.get(0));
        assertEquals("apple", suggestions.get(1));
        assertEquals("app", suggestions.get(2));
    }

    @Test
    @DisplayName("Should respect limit parameter in getSuggestions")
    void testGetSuggestionsLimit() {
        trie.insert("car", 10);
        trie.insert("cart", 20);
        trie.insert("card", 30);
        trie.insert("care", 40);
        
        List<String> suggestions = trie.getSuggestions("car", 2);
        assertEquals(2, suggestions.size());
        assertTrue(suggestions.contains("care"));
        assertTrue(suggestions.contains("card"));
    }

    @Test
    @DisplayName("Should return all words with a given prefix")
    void testGetAllWordsWithPrefix() {
        trie.insert("test");
        trie.insert("testing");
        trie.insert("tester");
        trie.insert("team"); // diff prefix
        
        List<String> words = trie.getAllWordsWithPrefix("test");
        assertEquals(3, words.size());
        assertTrue(words.containsAll(List.of("test", "testing", "tester")));
    }

    @Test
    @DisplayName("Should delete a word correctly and prune nodes if needed")
    void testDelete() {
        trie.insert("apple");
        trie.insert("application");
        
        trie.delete("apple");
        assertFalse(trie.search("apple"));
        assertTrue(trie.search("application"));
        assertEquals(1, trie.getSize());
    }

    @Test
    @DisplayName("Should track frequency correctly and allow updates")
    void testFrequencyTracking() {
        trie.insert("data", 5);
        assertEquals(5, trie.getFrequency("data"));
        
        trie.insert("data", 20); // update
        assertEquals(20, trie.getFrequency("data"));
        assertEquals(0, trie.getFrequency("missing"));
    }

    @Test
    @DisplayName("Should handle operations on an empty Trie gracefully")
    void testEmptyTrie() {
        assertFalse(trie.search("word"));
        assertFalse(trie.startsWith("w"));
        assertTrue(trie.getSuggestions("w", 5).isEmpty());
        assertTrue(trie.getAllWordsWithPrefix("w").isEmpty());
        assertEquals(0, trie.getSize());
        
        trie.delete("word"); // Should not throw exception
    }

    @Test
    @DisplayName("Should correctly handle single character words")
    void testSingleCharacterWords() {
        trie.insert("a", 10);
        trie.insert("i", 20);
        
        assertTrue(trie.search("a"));
        assertTrue(trie.search("i"));
        assertEquals(2, trie.getSize());
        assertEquals(20, trie.getFrequency("i"));
    }

    @Test
    @DisplayName("Should handle overlapping prefixes correctly")
    void testOverlappingPrefixes() {
        trie.insert("app");
        trie.insert("apple");
        trie.insert("application");
        
        assertTrue(trie.search("app"));
        assertTrue(trie.search("apple"));
        assertTrue(trie.search("application"));
        assertEquals(3, trie.getSize());
    }
    
    @Test
    @DisplayName("Should return empty list for non-existent prefix suggestions")
    void testSuggestionsNonExistentPrefix() {
        trie.insert("hello", 10);
        assertTrue(trie.getSuggestions("world", 5).isEmpty());
        assertTrue(trie.getAllWordsWithPrefix("world").isEmpty());
    }

    @Test
    @DisplayName("Should handle delete of a word that is a prefix of another")
    void testDeletePrefixWord() {
        trie.insert("win");
        trie.insert("window");
        
        trie.delete("win");
        
        assertFalse(trie.search("win"));
        assertTrue(trie.startsWith("win"));
        assertTrue(trie.search("window"));
        assertEquals(1, trie.getSize());
    }
}
