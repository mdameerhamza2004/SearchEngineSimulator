package com.searchengine.simulator.dsa.trie;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single node within the Trie data structure.
 * Stores references to children, word termination status, and optional metadata like frequency and the complete word.
 */
public class TrieNode {
    
    private final Map<Character, TrieNode> children = new HashMap<>();
    private boolean endOfWord = false;
    private int frequency = 0;
    private String word = null;

    /**
     * Checks if the current node has a specific child character.
     * Time Complexity: O(1) expected map lookup.
     *
     * @param c The character to check.
     * @return True if the child exists, false otherwise.
     */
    public boolean hasChild(char c) {
        return children.containsKey(c);
    }

    /**
     * Retrieves the child node for a specific character.
     * Time Complexity: O(1) expected map lookup.
     *
     * @param c The character of the child.
     * @return The TrieNode corresponding to the character, or null if it doesn't exist.
     */
    public TrieNode getChild(char c) {
        return children.get(c);
    }

    /**
     * Adds a new child node for the given character.
     * Time Complexity: O(1) expected map insertion.
     *
     * @param c The character to add.
     * @return The newly created or existing TrieNode.
     */
    public TrieNode addChild(char c) {
        return children.computeIfAbsent(c, k -> new TrieNode());
    }

    // Getters and Setters

    public Map<Character, TrieNode> getChildren() {
        return children;
    }

    public boolean isEndOfWord() {
        return endOfWord;
    }

    public void setEndOfWord(boolean endOfWord) {
        this.endOfWord = endOfWord;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
