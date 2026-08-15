package com.searchengine.simulator.dsa.index;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Thread-safe Inverted Index implementation mapping terms to lists of postings.
 * Allows efficient text search by retrieving documents that contain specified terms.
 */
public class InvertedIndex {
    
    // Core data structure mapping a term to its postings list
    private final Map<String, List<Posting>> index = new ConcurrentHashMap<>();
    
    // Tracks the total number of tokens (length) for each document
    private final Map<Long, Integer> documentLengths = new ConcurrentHashMap<>();
    
    // Tracks total indexed documents
    private int totalDocuments = 0;
    
    // Lock for compound operations, mainly to safely manage totalDocuments and atomic updates to multiple lists
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Adds a document to the index.
     * Time Complexity: O(T) where T is the number of tokens in the document.
     *
     * @param docId  The document ID.
     * @param tokens The ordered list of tokens (words) extracted from the document.
     */
    public void addDocument(long docId, List<String> tokens) {
        lock.writeLock().lock();
        try {
            if (documentLengths.containsKey(docId)) {
                // Document already exists, consider throwing an exception or removing first.
                // For simplicity, we just remove the old one first.
                removeDocumentInternal(docId);
            }

            documentLengths.put(docId, tokens.size());
            totalDocuments++;

            // Local aggregation of term positions
            Map<String, List<Integer>> positionsMap = new HashMap<>();
            for (int i = 0; i < tokens.size(); i++) {
                String token = tokens.get(i);
                positionsMap.computeIfAbsent(token, k -> new ArrayList<>()).add(i);
            }

            // Update the global inverted index
            for (Map.Entry<String, List<Integer>> entry : positionsMap.entrySet()) {
                String term = entry.getKey();
                List<Integer> positions = entry.getValue();
                Posting posting = new Posting(docId, positions.size(), positions);

                index.compute(term, (k, existingPostings) -> {
                    if (existingPostings == null) {
                        List<Posting> newList = new ArrayList<>();
                        newList.add(posting);
                        return newList;
                    }
                    existingPostings.add(posting);
                    return existingPostings;
                });
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Retrieves postings for a single term.
     * Time Complexity: O(1) expected for map lookup.
     *
     * @param term The term to look up.
     * @return List of postings, or an empty list if the term is not found.
     */
    public List<Posting> getPostings(String term) {
        lock.readLock().lock();
        try {
            return index.getOrDefault(term, Collections.emptyList());
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Searches for a single term and returns the set of document IDs containing it.
     * Time Complexity: O(P) where P is the number of postings for the term.
     *
     * @param term The term to search for.
     * @return A set of document IDs.
     */
    public Set<Long> searchSingle(String term) {
        return getPostings(term).stream()
                .map(Posting::docId)
                .collect(Collectors.toSet());
    }

    /**
     * Searches for documents containing ALL of the given terms (intersection).
     * Time Complexity: O(N * min(P_i)) roughly, where N is terms count and P_i is postings size.
     *
     * @param terms The list of terms.
     * @return A set of document IDs containing all terms.
     */
    public Set<Long> searchAnd(List<String> terms) {
        if (terms == null || terms.isEmpty()) return Collections.emptySet();
        
        lock.readLock().lock();
        try {
            Set<Long> result = searchSingle(terms.get(0));
            for (int i = 1; i < terms.size(); i++) {
                if (result.isEmpty()) break;
                result.retainAll(searchSingle(terms.get(i)));
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Searches for documents containing ANY of the given terms (union).
     * Time Complexity: O(sum(P_i)) where P_i is postings size for each term.
     *
     * @param terms The list of terms.
     * @return A set of document IDs containing at least one of the terms.
     */
    public Set<Long> searchOr(List<String> terms) {
        if (terms == null || terms.isEmpty()) return Collections.emptySet();
        
        lock.readLock().lock();
        try {
            Set<Long> result = new HashSet<>();
            for (String term : terms) {
                result.addAll(searchSingle(term));
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Removes a document and all its associated postings from the index.
     * Time Complexity: O(V) where V is the number of unique terms in the index.
     *
     * @param docId The ID of the document to remove.
     */
    public void removeDocument(long docId) {
        lock.writeLock().lock();
        try {
            removeDocumentInternal(docId);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    private void removeDocumentInternal(long docId) {
        if (!documentLengths.containsKey(docId)) return;
        
        // Remove postings for this docId from all terms
        Iterator<Map.Entry<String, List<Posting>>> iterator = index.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<Posting>> entry = iterator.next();
            List<Posting> postings = entry.getValue();
            postings.removeIf(p -> p.docId() == docId);
            
            // If the term has no more postings, remove it entirely
            if (postings.isEmpty()) {
                iterator.remove();
            }
        }
        
        documentLengths.remove(docId);
        totalDocuments--;
    }

    /**
     * Returns the total number of documents currently in the index.
     * Time Complexity: O(1)
     *
     * @return The count of indexed documents.
     */
    public int getTotalDocuments() {
        lock.readLock().lock();
        try {
            return totalDocuments;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Returns the length (total tokens) of a specific document.
     * Time Complexity: O(1)
     *
     * @param docId The document ID.
     * @return The number of tokens in the document, or 0 if not found.
     */
    public int getDocumentLength(long docId) {
        lock.readLock().lock();
        try {
            return documentLengths.getOrDefault(docId, 0);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Returns the document frequency (number of documents containing the term).
     * Time Complexity: O(1) expected for map lookup.
     *
     * @param term The term to query.
     * @return The number of documents containing the term.
     */
    public int getDocumentFrequency(String term) {
        lock.readLock().lock();
        try {
            return getPostings(term).size();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Returns all unique terms present in the index.
     * Time Complexity: O(V) where V is the number of unique terms.
     *
     * @return A set of all indexed terms.
     */
    public Set<String> getAllTerms() {
        lock.readLock().lock();
        try {
            return new HashSet<>(index.keySet());
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Clears the entire index.
     * Time Complexity: O(1) to drop references (GC handles the rest).
     */
    public void clear() {
        lock.writeLock().lock();
        try {
            index.clear();
            documentLengths.clear();
            totalDocuments = 0;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
