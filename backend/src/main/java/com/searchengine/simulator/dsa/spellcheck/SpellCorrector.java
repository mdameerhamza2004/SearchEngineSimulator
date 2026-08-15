package com.searchengine.simulator.dsa.spellcheck;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Suggests spelling corrections for search queries using a dictionary and Levenshtein Distance.
 */
public class SpellCorrector {

    private final Set<String> dictionary = new HashSet<>();
    private final LevenshteinDistance levenshtein = new LevenshteinDistance();
    private int maxDistance = 2; // Configurable threshold

    /**
     * Populates the dictionary with a collection of words.
     * @param words Collection of known valid words
     */
    public void buildDictionary(Collection<String> words) {
        if (words != null) {
            for (String word : words) {
                if (word != null) {
                    dictionary.add(word.toLowerCase());
                }
            }
        }
    }

    /**
     * Adds a single word to the dictionary.
     * @param word The word to add
     */
    public void addWord(String word) {
        if (word != null) {
            dictionary.add(word.toLowerCase());
        }
    }

    /**
     * Checks if a word exists in the known dictionary.
     * @param word Word to check
     * @return True if word is in dictionary, false otherwise
     */
    public boolean isKnownWord(String word) {
        if (word == null) return false;
        return dictionary.contains(word.toLowerCase());
    }

    /**
     * Sets the maximum edit distance for suggestions.
     * @param maxDistance Maximum allowed distance
     */
    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    private record Suggestion(String word, int distance) implements Comparable<Suggestion> {
        @Override
        public int compareTo(Suggestion other) {
            int distCompare = Integer.compare(this.distance, other.distance);
            if (distCompare != 0) {
                return distCompare;
            }
            return this.word.compareTo(other.word);
        }
    }

    /**
     * Suggests corrections for a misspelled word based on edit distance.
     * Uses a PriorityQueue to keep track of the best matches.
     *
     * @param word           The potentially misspelled word
     * @param maxSuggestions Maximum number of suggestions to return
     * @return List of suggested corrections, sorted by proximity
     */
    public List<String> suggest(String word, int maxSuggestions) {
        if (word == null || word.isEmpty()) {
            return new ArrayList<>();
        }

        String lowerWord = word.toLowerCase();

        // If the word is already in the dictionary, no correction is needed.
        if (isKnownWord(lowerWord)) {
            return List.of(lowerWord);
        }

        PriorityQueue<Suggestion> pq = new PriorityQueue<>();

        for (String dictWord : dictionary) {
            int dist = levenshtein.calculate(lowerWord, dictWord);
            if (dist <= maxDistance) {
                pq.offer(new Suggestion(dictWord, dist));
            }
        }

        List<String> suggestions = new ArrayList<>();
        while (!pq.isEmpty() && suggestions.size() < maxSuggestions) {
            suggestions.add(pq.poll().word());
        }

        return suggestions;
    }

    /**
     * Analyzes a query to find misspellings and provides suggestions for each.
     *
     * @param queryTerms List of terms in the query
     * @return Map of misspelled term to its list of suggestions
     */
    public Map<String, List<String>> checkQuery(List<String> queryTerms) {
        Map<String, List<String>> misspelledMap = new HashMap<>();

        if (queryTerms == null) return misspelledMap;

        for (String term : queryTerms) {
            String lowerTerm = term.toLowerCase();
            if (!isKnownWord(lowerTerm)) {
                List<String> termSuggestions = suggest(lowerTerm, 3); // top 3 suggestions by default
                misspelledMap.put(lowerTerm, termSuggestions);
            }
        }

        return misspelledMap;
    }

    /**
     * Returns the best-guess corrected query string.
     * Replaces misspelled words with their top suggestion, if any exists.
     *
     * @param queryTerms List of terms in the original query
     * @return The corrected query string
     */
    public String correctQuery(List<String> queryTerms) {
        if (queryTerms == null || queryTerms.isEmpty()) {
            return "";
        }

        StringBuilder corrected = new StringBuilder();

        for (int i = 0; i < queryTerms.size(); i++) {
            String term = queryTerms.get(i).toLowerCase();
            
            if (isKnownWord(term)) {
                corrected.append(term);
            } else {
                List<String> termSuggestions = suggest(term, 1);
                if (!termSuggestions.isEmpty()) {
                    corrected.append(termSuggestions.get(0));
                } else {
                    corrected.append(term); // Fallback to original term if no suggestions
                }
            }

            if (i < queryTerms.size() - 1) {
                corrected.append(" ");
            }
        }

        return corrected.toString();
    }
}
