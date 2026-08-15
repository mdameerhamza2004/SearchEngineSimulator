package com.searchengine.simulator.dsa.text;

import java.util.ArrayList;
import java.util.List;

/**
 * TextProcessor orchestrates the full text processing pipeline.
 * Coordinates Tokenizer, StopWordFilter, and PorterStemmer.
 * 
 * @author Search Engine Simulator
 */
public class TextProcessor {
    private final Tokenizer tokenizer;
    private final StopWordFilter stopWordFilter;
    private final PorterStemmer stemmer;
    
    private final boolean enableStemming;
    private final boolean enableStopWordFilter;

    /**
     * Default constructor enables all pipeline steps.
     */
    public TextProcessor() {
        this(true, true);
    }

    /**
     * Constructor allowing specific pipeline steps to be disabled.
     * 
     * @param enableStopWordFilter Whether to filter stop words.
     * @param enableStemming Whether to stem words.
     */
    public TextProcessor(boolean enableStopWordFilter, boolean enableStemming) {
        this.tokenizer = new Tokenizer();
        this.stopWordFilter = new StopWordFilter();
        this.stemmer = new PorterStemmer();
        this.enableStopWordFilter = enableStopWordFilter;
        this.enableStemming = enableStemming;
    }

    /**
     * Processes text through the configured pipeline.
     * 
     * @param text The input text.
     * @return A list of processed tokens.
     */
    public List<String> process(String text) {
        List<String> tokens = tokenizer.tokenize(text);
        
        if (enableStopWordFilter) {
            tokens = stopWordFilter.filter(tokens);
        }
        
        if (enableStemming) {
            List<String> stemmedTokens = new ArrayList<>(tokens.size());
            for (String token : tokens) {
                stemmedTokens.add(stemmer.stem(token));
            }
            tokens = stemmedTokens;
        }
        
        return tokens;
    }

    /**
     * Only tokenizes the text (with lowercase conversion).
     * 
     * @param text The input text.
     * @return Tokenized list.
     */
    public List<String> tokenizeOnly(String text) {
        return tokenizer.tokenize(text);
    }

    /**
     * Tokenizes and removes stop words, bypassing stemming.
     * 
     * @param text The input text.
     * @return Tokenized and filtered list.
     */
    public List<String> processWithoutStemming(String text) {
        List<String> tokens = tokenizer.tokenize(text);
        if (enableStopWordFilter) {
            tokens = stopWordFilter.filter(tokens);
        }
        return tokens;
    }
}
