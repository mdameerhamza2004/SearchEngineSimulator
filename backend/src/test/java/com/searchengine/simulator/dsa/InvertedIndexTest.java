package com.searchengine.simulator.dsa;

import com.searchengine.simulator.dsa.index.InvertedIndex;
import com.searchengine.simulator.dsa.index.Posting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class InvertedIndexTest {

    private InvertedIndex index;

    @BeforeEach
    void setUp() {
        index = new InvertedIndex();
    }

    @Test
    @DisplayName("Should add document and allow single term search")
    void testAddDocumentAndSingleSearch() {
        index.addDocument(1L, Arrays.asList("hello", "world", "hello"));
        Set<Long> docs = index.searchSingle("hello");
        
        assertEquals(1, docs.size());
        assertTrue(docs.contains(1L));
        assertEquals(1, index.getTotalDocuments());
    }

    @Test
    @DisplayName("Should correctly evaluate multi-term AND search (intersection)")
    void testSearchAnd() {
        index.addDocument(1L, Arrays.asList("apple", "banana"));
        index.addDocument(2L, Arrays.asList("apple", "orange"));
        index.addDocument(3L, Arrays.asList("banana", "orange"));
        
        Set<Long> result = index.searchAnd(Arrays.asList("apple", "banana"));
        assertEquals(1, result.size());
        assertTrue(result.contains(1L));
    }

    @Test
    @DisplayName("Should correctly evaluate multi-term OR search (union)")
    void testSearchOr() {
        index.addDocument(1L, Arrays.asList("apple", "banana"));
        index.addDocument(2L, Arrays.asList("apple", "orange"));
        index.addDocument(3L, Arrays.asList("grape"));
        
        Set<Long> result = index.searchOr(Arrays.asList("banana", "orange"));
        assertEquals(2, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
    }

    @Test
    @DisplayName("Should remove document and all its postings")
    void testRemoveDocument() {
        index.addDocument(1L, Arrays.asList("apple", "banana"));
        index.addDocument(2L, Arrays.asList("apple"));
        
        index.removeDocument(1L);
        
        assertEquals(1, index.getTotalDocuments());
        Set<Long> appleDocs = index.searchSingle("apple");
        assertEquals(1, appleDocs.size());
        assertTrue(appleDocs.contains(2L));
        
        Set<Long> bananaDocs = index.searchSingle("banana");
        assertTrue(bananaDocs.isEmpty());
    }

    @Test
    @DisplayName("Should correctly track document frequency")
    void testDocumentFrequency() {
        index.addDocument(1L, Arrays.asList("apple", "banana"));
        index.addDocument(2L, Arrays.asList("apple", "orange"));
        index.addDocument(3L, Arrays.asList("apple"));
        
        assertEquals(3, index.getDocumentFrequency("apple"));
        assertEquals(1, index.getDocumentFrequency("banana"));
        assertEquals(0, index.getDocumentFrequency("grape"));
    }

    @Test
    @DisplayName("Should accurately record term positions within documents")
    void testTermPositions() {
        index.addDocument(1L, Arrays.asList("to", "be", "or", "not", "to", "be"));
        List<Posting> postings = index.getPostings("to");
        
        assertEquals(1, postings.size());
        Posting p = postings.get(0);
        assertEquals(2, p.termFrequency());
        assertEquals(Arrays.asList(0, 4), p.positions());
    }

    @Test
    @DisplayName("Should handle operations gracefully when index is empty")
    void testEmptyIndex() {
        assertTrue(index.searchSingle("test").isEmpty());
        assertTrue(index.searchAnd(Arrays.asList("test")).isEmpty());
        assertTrue(index.searchOr(Arrays.asList("test")).isEmpty());
        assertEquals(0, index.getDocumentFrequency("test"));
        assertEquals(0, index.getTotalDocuments());
        assertEquals(0, index.getDocumentLength(1L));
        assertTrue(index.getAllTerms().isEmpty());
    }

    @Test
    @DisplayName("Should index multiple documents containing the same term")
    void testMultipleDocumentsSameTerm() {
        for (long i = 1; i <= 5; i++) {
            index.addDocument(i, Arrays.asList("java", "programming"));
        }
        
        Set<Long> javaDocs = index.searchSingle("java");
        assertEquals(5, javaDocs.size());
        for (long i = 1; i <= 5; i++) {
            assertTrue(javaDocs.contains(i));
        }
    }

    @Test
    @DisplayName("Should overwrite document if added with same ID")
    void testOverwriteDocument() {
        index.addDocument(1L, Arrays.asList("hello", "world"));
        assertEquals(2, index.getDocumentLength(1L));
        
        // Overwrite
        index.addDocument(1L, Arrays.asList("hello"));
        assertEquals(1, index.getDocumentLength(1L));
        assertEquals(1, index.getTotalDocuments());
        
        assertTrue(index.searchSingle("world").isEmpty());
    }

    @Test
    @DisplayName("Should return all indexed terms")
    void testGetAllTerms() {
        index.addDocument(1L, Arrays.asList("java", "python"));
        index.addDocument(2L, Arrays.asList("python", "c++"));
        
        Set<String> terms = index.getAllTerms();
        assertEquals(3, terms.size());
        assertTrue(terms.containsAll(Arrays.asList("java", "python", "c++")));
    }

    @Test
    @DisplayName("Should return correct document lengths")
    void testDocumentLength() {
        index.addDocument(1L, Arrays.asList("one"));
        index.addDocument(2L, Arrays.asList("one", "two"));
        index.addDocument(3L, Arrays.asList("one", "two", "three"));
        
        assertEquals(1, index.getDocumentLength(1L));
        assertEquals(2, index.getDocumentLength(2L));
        assertEquals(3, index.getDocumentLength(3L));
    }

    @Test
    @DisplayName("Should clear index successfully")
    void testClearIndex() {
        index.addDocument(1L, Arrays.asList("hello", "world"));
        index.clear();
        
        assertEquals(0, index.getTotalDocuments());
        assertTrue(index.getAllTerms().isEmpty());
        assertTrue(index.searchSingle("hello").isEmpty());
    }
}
