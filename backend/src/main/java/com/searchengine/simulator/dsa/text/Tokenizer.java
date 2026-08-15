package com.searchengine.simulator.dsa.text;

import java.util.ArrayList;
import java.util.List;

/**
 * Tokenizer component for the search engine text processing pipeline.
 * Splits text into lowercase alphanumeric tokens, removing punctuation and special characters.
 * 
 * @author Search Engine Simulator
 */
public class Tokenizer {

    /**
     * Splits text into lowercase alphanumeric tokens.
     * 
     * Time complexity: O(n) where n is the length of the text.
     * Space complexity: O(n) for the output list of tokens.
     * 
     * @param text The input text to tokenize.
     * @return A list of normalized alphanumeric tokens.
     */
    public List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            return tokens;
        }

        int len = text.length();
        StringBuilder currentToken = new StringBuilder();

        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                currentToken.append(Character.toLowerCase(c));
            } else {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
            }
        }
        
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }
}
