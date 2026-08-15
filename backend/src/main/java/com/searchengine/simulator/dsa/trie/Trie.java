package com.searchengine.simulator.dsa.trie;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * A Trie (Prefix Tree) implementation tailored for search engines.
 * Supports efficient prefix matching, exact search, and auto-completion suggestions ranked by frequency.
 */
public class Trie {

    private final TrieNode root = new TrieNode();
    private int size = 0;

    /**
     * Inserts a word into the Trie with a specified frequency.
     * If the word already exists, its frequency is updated.
     * 
     * Time Complexity: O(L) where L is the length of the word.
     *
     * @param word      The word to insert.
     * @param frequency The frequency (popularity) of the word.
     */
    public void insert(String word, int frequency) {
        if (word == null || word.isEmpty()) return;
        
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            current = current.addChild(c);
        }
        
        if (!current.isEndOfWord()) {
            current.setEndOfWord(true);
            size++;
        }
        
        current.setWord(word);
        current.setFrequency(frequency);
    }

    /**
     * Inserts a word into the Trie with a default frequency of 1.
     * 
     * Time Complexity: O(L) where L is the length of the word.
     *
     * @param word The word to insert.
     */
    public void insert(String word) {
        insert(word, 1);
    }

    /**
     * Searches for an exact word match in the Trie.
     * 
     * Time Complexity: O(L) where L is the length of the word.
     *
     * @param word The word to search for.
     * @return True if the exact word is found, false otherwise.
     */
    public boolean search(String word) {
        TrieNode node = getNode(word);
        return node != null && node.isEndOfWord();
    }

    /**
     * Checks if there is any word in the Trie that starts with the given prefix.
     * 
     * Time Complexity: O(P) where P is the length of the prefix.
     *
     * @param prefix The prefix to check.
     * @return True if a matching prefix exists, false otherwise.
     */
    public boolean startsWith(String prefix) {
        return getNode(prefix) != null;
    }

    /**
     * Returns top-K suggestions for a given prefix, ranked by frequency.
     * Uses a min-heap to efficiently extract the top results.
     * 
     * Time Complexity: O(P + N * log(K)) where P is prefix length, N is nodes in the subtree, and K is limit.
     *
     * @param prefix The prefix to search for.
     * @param limit  The maximum number of suggestions to return.
     * @return A list of suggested words.
     */
    public List<String> getSuggestions(String prefix, int limit) {
        if (prefix == null || limit <= 0) return new ArrayList<>();
        
        TrieNode prefixNode = getNode(prefix);
        if (prefixNode == null) return new ArrayList<>();

        // Min-heap to keep track of the top 'limit' words by frequency
        PriorityQueue<TrieNode> minHeap = new PriorityQueue<>(
            Comparator.comparingInt(TrieNode::getFrequency)
        );

        // Perform DFS from the prefix node
        collectTopWordsDFS(prefixNode, minHeap, limit);

        // Extract from min-heap (will be in ascending order of frequency)
        List<String> suggestions = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            suggestions.add(0, minHeap.poll().getWord()); // Add to front for descending order
        }
        
        return suggestions;
    }

    /**
     * Returns all words in the Trie that start with the given prefix.
     * 
     * Time Complexity: O(P + N) where P is prefix length and N is nodes in the subtree.
     *
     * @param prefix The prefix to search for.
     * @return A list of all matching words.
     */
    public List<String> getAllWordsWithPrefix(String prefix) {
        List<String> result = new ArrayList<>();
        if (prefix == null) return result;
        
        TrieNode prefixNode = getNode(prefix);
        if (prefixNode != null) {
            collectAllWordsDFS(prefixNode, result);
        }
        return result;
    }

    /**
     * Removes a word from the Trie.
     * 
     * Time Complexity: O(L) where L is the length of the word.
     *
     * @param word The word to remove.
     */
    public void delete(String word) {
        if (word == null || word.isEmpty()) return;
        if (deleteDFS(root, word, 0)) {
            size--;
        }
    }

    /**
     * Gets the frequency of a specific word.
     * 
     * Time Complexity: O(L) where L is the length of the word.
     *
     * @param word The word to check.
     * @return The frequency of the word, or 0 if not found.
     */
    public int getFrequency(String word) {
        TrieNode node = getNode(word);
        return (node != null && node.isEndOfWord()) ? node.getFrequency() : 0;
    }

    /**
     * Returns the number of distinct words in the Trie.
     * 
     * Time Complexity: O(1)
     *
     * @return The number of words.
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the root node of the Trie (useful for visualization or testing).
     * 
     * @return The root TrieNode.
     */
    public TrieNode getRoot() {
        return root;
    }

    // --- Helper Methods ---

    /**
     * Helper to navigate to the node representing the end of the given string.
     */
    private TrieNode getNode(String str) {
        if (str == null) return null;
        TrieNode current = root;
        for (char c : str.toCharArray()) {
            if (!current.hasChild(c)) return null;
            current = current.getChild(c);
        }
        return current;
    }

    /**
     * DFS helper to collect top-K frequent words into a min-heap.
     */
    private void collectTopWordsDFS(TrieNode node, PriorityQueue<TrieNode> minHeap, int limit) {
        if (node.isEndOfWord()) {
            minHeap.offer(node);
            if (minHeap.size() > limit) {
                minHeap.poll(); // Remove the least frequent word from the top-K
            }
        }
        
        for (TrieNode child : node.getChildren().values()) {
            collectTopWordsDFS(child, minHeap, limit);
        }
    }

    /**
     * DFS helper to collect all words in the subtree.
     */
    private void collectAllWordsDFS(TrieNode node, List<String> result) {
        if (node.isEndOfWord()) {
            result.add(node.getWord());
        }
        for (TrieNode child : node.getChildren().values()) {
            collectAllWordsDFS(child, result);
        }
    }

    /**
     * Recursive helper to delete a word and optionally prune empty nodes.
     * Returns true if the word was successfully deleted.
     */
    private boolean deleteDFS(TrieNode current, String word, int index) {
        if (index == word.length()) {
            if (!current.isEndOfWord()) {
                return false; // Word doesn't exist
            }
            current.setEndOfWord(false);
            current.setWord(null);
            current.setFrequency(0);
            return true;
        }

        char c = word.charAt(index);
        TrieNode node = current.getChild(c);
        if (node == null) {
            return false;
        }

        boolean shouldDeleteWord = deleteDFS(node, word, index + 1);

        // Pruning: if node has no children and is not end of another word, remove it from parent
        if (shouldDeleteWord && !node.isEndOfWord() && node.getChildren().isEmpty()) {
            current.getChildren().remove(c);
        }

        return shouldDeleteWord;
    }
}
