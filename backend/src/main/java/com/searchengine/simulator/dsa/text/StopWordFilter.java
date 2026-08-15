package com.searchengine.simulator.dsa.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stop word filter component for the search engine text processing pipeline.
 * Filters out common English words to improve search relevance.
 * 
 * @author Search Engine Simulator
 */
public class StopWordFilter {
    private final Set<String> stopWords;

    /**
     * Default constructor loading a hardcoded set of common stop words.
     */
    public StopWordFilter() {
        this.stopWords = new HashSet<>(Arrays.asList(
            "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't",
            "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can't",
            "cannot", "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't", "down",
            "during", "each", "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have", "haven't",
            "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him", "himself",
            "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't", "it", "it's",
            "its", "itself", "let's", "me", "more", "most", "mustn't", "my", "myself", "no", "nor", "not", "of", "off",
            "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves", "out", "over", "own", "same",
            "shan't", "she", "she'd", "she'll", "she's", "should", "shouldn't", "so", "some", "such", "than", "that",
            "that's", "the", "their", "theirs", "them", "themselves", "then", "there", "there's", "these", "they",
            "they'd", "they'll", "they're", "they've", "this", "those", "through", "to", "too", "under", "until",
            "up", "very", "was", "wasn't", "we", "we'd", "we'll", "we're", "we've", "were", "weren't", "what", "what's",
            "when", "when's", "where", "where's", "which", "while", "who", "who's", "whom", "why", "why's", "with",
            "won't", "would", "wouldn't", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself",
            "yourselves"
        ));
    }

    /**
     * Constructor that loads stop words from an InputStream.
     * 
     * @param inputStream The input stream to read stop words from (one word per line).
     * @throws IOException If an I/O error occurs.
     */
    public StopWordFilter(InputStream inputStream) throws IOException {
        this.stopWords = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim().toLowerCase();
                if (!word.isEmpty()) {
                    this.stopWords.add(word);
                }
            }
        }
    }

    /**
     * Filters a list of tokens, removing any stop words.
     * 
     * Time complexity: O(n) where n is the number of tokens.
     * Space complexity: O(n) for the output list.
     * 
     * @param tokens The list of tokens to filter.
     * @return A new list of tokens with stop words removed.
     */
    public List<String> filter(List<String> tokens) {
        if (tokens == null) {
            return new ArrayList<>();
        }
        List<String> filtered = new ArrayList<>(tokens.size());
        for (String token : tokens) {
            if (!isStopWord(token)) {
                filtered.add(token);
            }
        }
        return filtered;
    }

    /**
     * Checks if a specific word is a stop word.
     * 
     * Time complexity: O(1) expected time for HashSet lookup.
     * 
     * @param word The word to check.
     * @return true if the word is a stop word, false otherwise.
     */
    public boolean isStopWord(String word) {
        return word != null && stopWords.contains(word.toLowerCase());
    }
}
