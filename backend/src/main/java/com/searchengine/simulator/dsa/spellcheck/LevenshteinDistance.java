package com.searchengine.simulator.dsa.spellcheck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Calculates the Levenshtein distance between two strings using Dynamic Programming.
 * The Levenshtein distance is a string metric for measuring the difference between two sequences.
 * It is the minimum number of single-character edits (insertions, deletions, or substitutions)
 * required to change one word into the other.
 */
public class LevenshteinDistance {

    /**
     * Calculates the minimum edit distance between source and target strings.
     * Time Complexity: O(m * n) where m is source length and n is target length.
     * Space Complexity: O(m * n) for the DP matrix.
     *
     * @param source The starting string
     * @param target The target string
     * @return The minimum number of edits required
     */
    public int calculate(String source, String target) {
        return getDistanceMatrix(source, target)[source == null ? 0 : source.length()][target == null ? 0 : target.length()];
    }

    /**
     * Computes the full Dynamic Programming matrix for Levenshtein Distance.
     *
     * @param source The starting string
     * @param target The target string
     * @return The 2D DP matrix
     */
    public int[][] getDistanceMatrix(String source, String target) {
        if (source == null) source = "";
        if (target == null) target = "";

        int m = source.length();
        int n = target.length();

        int[][] dp = new int[m + 1][n + 1];

        // Base cases: transforming an empty string to another string requires N insertions
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i; // Deletions from source to reach empty target
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j; // Insertions to empty source to reach target
        }

        // Fill the DP matrix
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                // If the characters match, no operation is needed (cost is 0), so we take the diagonal value.
                if (source.charAt(i - 1) == target.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    // We take the minimum cost of the three possible operations:
                    // 1. Substitute: take diagonal (i-1, j-1) and add 1
                    // 2. Insert: take top (i, j-1) and add 1
                    // 3. Delete: take left (i-1, j) and add 1
                    int substitute = dp[i - 1][j - 1] + 1;
                    int insert = dp[i][j - 1] + 1;
                    int delete = dp[i - 1][j] + 1;

                    dp[i][j] = Math.min(substitute, Math.min(insert, delete));
                }
            }
        }

        return dp;
    }

    /**
     * Backtracks through the DP matrix to return the sequence of operations.
     * 
     * @param source The starting string
     * @param target The target string
     * @return List of operations (e.g., "MATCH: a", "INSERT: b", "DELETE: c", "SUBSTITUTE: d -> e")
     */
    public List<String> getOperations(String source, String target) {
        if (source == null) source = "";
        if (target == null) target = "";

        int[][] dp = getDistanceMatrix(source, target);
        List<String> operations = new ArrayList<>();
        
        int i = source.length();
        int j = target.length();

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && source.charAt(i - 1) == target.charAt(j - 1)) {
                operations.add("MATCH: " + source.charAt(i - 1));
                i--;
                j--;
            } else if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + 1) {
                operations.add("SUBSTITUTE: " + source.charAt(i - 1) + " -> " + target.charAt(j - 1));
                i--;
                j--;
            } else if (j > 0 && dp[i][j] == dp[i][j - 1] + 1) {
                operations.add("INSERT: " + target.charAt(j - 1));
                j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
                operations.add("DELETE: " + source.charAt(i - 1));
                i--;
            }
        }

        Collections.reverse(operations);
        return operations;
    }
}
