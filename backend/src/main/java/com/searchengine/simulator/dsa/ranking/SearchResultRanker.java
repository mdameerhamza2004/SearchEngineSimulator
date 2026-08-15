package com.searchengine.simulator.dsa.ranking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Ranks search results using efficient heap-based data structures.
 */
public class SearchResultRanker {

    /**
     * Represents a document and its computed score.
     */
    public record ScoredDocument(long docId, double score) implements Comparable<ScoredDocument> {
        @Override
        public int compareTo(ScoredDocument other) {
            // We want primary sorting by score. 
            // Since we'll use min-heap for top-K and max-heap for rankAll,
            // standard comparison by score (ascending) is fine, and we reverse it when needed.
            int scoreComparison = Double.compare(this.score, other.score);
            if (scoreComparison != 0) {
                return scoreComparison;
            }
            // Tie-breaking by docId (smaller ID preferred if scores tie)
            return Long.compare(other.docId, this.docId); // Reverse order for tie breaking in min-heap
        }
    }

    /**
     * Extracts the top-K highest scoring documents.
     * 
     * Why a MIN-heap of size K?
     * Instead of a max-heap of size N (which takes O(N) to build and O(K log N) to extract),
     * or sorting all N elements in O(N log N) time, we maintain a min-heap of size K.
     * As we iterate through N documents, we only push to the heap if the document's score
     * is greater than the current minimum in the heap (the root).
     * This ensures the heap always contains the K highest elements seen so far.
     * Complexity: O(N log K) time, O(K) space.
     *
     * @param documentScores Map of document ID to score
     * @param k              Number of top documents to extract
     * @return List of top K ScoredDocuments in descending order of score
     */
    public List<ScoredDocument> rankTopK(Map<Long, Double> documentScores, int k) {
        if (k <= 0 || documentScores == null || documentScores.isEmpty()) {
            return new ArrayList<>();
        }

        // Min-heap of size K
        PriorityQueue<ScoredDocument> minHeap = new PriorityQueue<>();

        for (Map.Entry<Long, Double> entry : documentScores.entrySet()) {
            ScoredDocument current = new ScoredDocument(entry.getKey(), entry.getValue());
            minHeap.offer(current);

            // Keep only the top K elements
            if (minHeap.size() > k) {
                minHeap.poll(); // Remove the lowest score currently in the heap
            }
        }

        // Extract from min-heap (will be in ascending order)
        List<ScoredDocument> result = new ArrayList<>(minHeap.size());
        while (!minHeap.isEmpty()) {
            result.add(minHeap.poll());
        }

        // Reverse to get descending order (highest score first)
        Collections.reverse(result);
        return result;
    }

    /**
     * Ranks all documents in descending order of their score.
     * Uses a max-heap to extract elements in order.
     * Complexity: O(N log N) time, O(N) space where N is number of documents.
     *
     * @param documentScores Map of document ID to score
     * @return List of all ScoredDocuments in descending order of score
     */
    public List<ScoredDocument> rankAll(Map<Long, Double> documentScores) {
        if (documentScores == null || documentScores.isEmpty()) {
            return new ArrayList<>();
        }

        // Max-heap
        PriorityQueue<ScoredDocument> maxHeap = new PriorityQueue<>(Collections.reverseOrder());

        for (Map.Entry<Long, Double> entry : documentScores.entrySet()) {
            maxHeap.offer(new ScoredDocument(entry.getKey(), entry.getValue()));
        }

        List<ScoredDocument> result = new ArrayList<>(maxHeap.size());
        while (!maxHeap.isEmpty()) {
            result.add(maxHeap.poll());
        }

        return result;
    }
}
