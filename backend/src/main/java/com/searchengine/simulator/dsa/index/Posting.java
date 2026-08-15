package com.searchengine.simulator.dsa.index;

import java.util.List;

/**
 * Represents a single document posting within the Inverted Index.
 *
 * <p>A posting records where and how frequently a specific term appears in a document.
 * This is the fundamental unit stored in the inverted index's postings lists.
 *
 * <p><b>Fields:</b>
 * <ul>
 *     <li>{@code docId} — Unique identifier of the document</li>
 *     <li>{@code termFrequency} — Number of times the term appears in this document</li>
 *     <li>{@code positions} — Ordered list of positions (0-indexed) where the term occurs</li>
 * </ul>
 *
 * <p><b>Example:</b> For the document "to be or not to be" (docId=1),
 * the term "to" would produce: {@code Posting(1, 2, [0, 4])}
 *
 * @author SearchEngineSimulator
 */
public record Posting(long docId, int termFrequency, List<Integer> positions) {

    /**
     * Convenience constructor for cases where positions are not tracked.
     *
     * @param docId         The document ID.
     * @param termFrequency The number of times the term appears.
     */
    public Posting(long docId, int termFrequency) {
        this(docId, termFrequency, List.of());
    }
}
