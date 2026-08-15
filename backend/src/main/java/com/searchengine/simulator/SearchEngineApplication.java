package com.searchengine.simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Search Engine Simulator — Application Entry Point.
 *
 * <p>A portfolio-grade search engine simulator that demonstrates core
 * Data Structures and Algorithms (DSA) through a full-stack application.
 *
 * <p>Core DSA components implemented from scratch:
 * <ul>
 *     <li>Inverted Index (HashMap-based word → document mapping)</li>
 *     <li>Trie (Autocomplete with prefix suggestions)</li>
 *     <li>TF-IDF (Term Frequency–Inverse Document Frequency ranking)</li>
 *     <li>Priority Queue / Heap (Top-K result extraction)</li>
 *     <li>Levenshtein Distance (Spell correction via edit distance)</li>
 *     <li>Text Processing (Tokenization, stop words, Porter Stemmer)</li>
 * </ul>
 *
 * @author SearchEngineSimulator
 * @version 1.0.0
 */
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SearchEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchEngineApplication.class, args);
    }
}
