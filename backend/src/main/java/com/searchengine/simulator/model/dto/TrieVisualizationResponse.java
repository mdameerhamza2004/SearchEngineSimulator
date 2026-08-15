package com.searchengine.simulator.model.dto;

import java.util.List;
import java.util.Map;

/**
 * Response DTO for Trie visualization endpoint.
 * Contains the tree structure as nodes/edges, the traversal path for a prefix,
 * and the resulting autocomplete suggestions.
 */
public record TrieVisualizationResponse(
        String prefix,
        List<TrieNodeDto> nodes,
        List<TrieEdgeDto> edges,
        List<String> traversalPath,     // node IDs along the prefix path
        List<String> suggestions,
        int totalWords
) {
    public record TrieNodeDto(
            String id,          // unique identifier e.g. "root", "r-p", "r-p-r"
            String character,   // single character or "ROOT"
            boolean isEndOfWord,
            int frequency,
            String word,        // full word if end-of-word
            int depth,
            boolean inTraversalPath,
            boolean isSuggestion
    ) {}

    public record TrieEdgeDto(
            String fromId,
            String toId,
            String character,
            boolean inTraversalPath
    ) {}
}
