package com.searchengine.simulator.service;

import com.searchengine.simulator.dsa.index.InvertedIndex;
import com.searchengine.simulator.dsa.index.Posting;
import com.searchengine.simulator.dsa.trie.Trie;
import com.searchengine.simulator.dsa.ranking.TfIdfScorer;
import com.searchengine.simulator.dsa.ranking.SearchResultRanker;
import com.searchengine.simulator.dsa.ranking.SearchResultRanker.ScoredDocument;
import com.searchengine.simulator.dsa.spellcheck.SpellCorrector;
import com.searchengine.simulator.dsa.text.TextProcessor;
import com.searchengine.simulator.model.dto.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SearchEngineService {

    private final InvertedIndex invertedIndex = new InvertedIndex();
    private final Trie trie = new Trie();
    private final TfIdfScorer tfIdfScorer = new TfIdfScorer();
    private final SearchResultRanker searchResultRanker = new SearchResultRanker();
    private final SpellCorrector spellCorrector = new SpellCorrector();
    private final TextProcessor textProcessor = new TextProcessor();
    private final TextProcessor textProcessorNoStemming = new TextProcessor(false, false);

    private final Map<Long, String> documentTitles = new ConcurrentHashMap<>();
    private final Map<Long, String> documentContents = new ConcurrentHashMap<>();
    private final Map<Long, String> documentCategories = new ConcurrentHashMap<>();

    public void indexDocument(long docId, String title, String content, String category) {
        String fullText = title + " " + content;
        
        // Add to inverted index (stemmed)
        List<String> tokens = textProcessor.process(fullText);
        invertedIndex.addDocument(docId, tokens);
        
        // Add to trie and spell checker (unstemmed)
        List<String> rawTokens = textProcessorNoStemming.process(fullText);
        Map<String, Integer> wordFrequencies = new HashMap<>();
        for (String token : rawTokens) {
            wordFrequencies.put(token, wordFrequencies.getOrDefault(token, 0) + 1);
        }
        
        for (Map.Entry<String, Integer> entry : wordFrequencies.entrySet()) {
            trie.insert(entry.getKey(), entry.getValue());
        }
        
        Set<String> uniqueRawTokens = new HashSet<>(rawTokens);
        spellCorrector.buildDictionary(new ArrayList<>(uniqueRawTokens));
        
        // Store for retrieval
        documentTitles.put(docId, title);
        documentContents.put(docId, content);
        if (category != null) {
            documentCategories.put(docId, category);
        }
    }

    public void removeDocument(long docId) {
        invertedIndex.removeDocument(docId);
        documentTitles.remove(docId);
        documentContents.remove(docId);
        documentCategories.remove(docId);
    }

    public SearchResponse search(String query, int limit) {
        long startTime = System.nanoTime();
        
        List<String> stemmedTerms = textProcessor.process(query);
        List<String> unstemmedTerms = textProcessorNoStemming.process(query);
        
        Map<String, List<String>> spellCheck = spellCorrector.checkQuery(unstemmedTerms);
        String correctedQuery = spellCorrector.correctQuery(unstemmedTerms);
        boolean hasCorrections = !query.equalsIgnoreCase(correctedQuery) && !spellCheck.isEmpty();
        
        Set<Long> docIds = invertedIndex.searchOr(stemmedTerms);
        List<String> finalTerms = stemmedTerms;
        String suggestedQuery = null;
        
        if (docIds.isEmpty() && hasCorrections) {
            suggestedQuery = correctedQuery;
            finalTerms = textProcessor.process(correctedQuery);
            docIds = invertedIndex.searchOr(finalTerms);
        } else if (hasCorrections) {
            suggestedQuery = correctedQuery;
        }

        Map<String, List<Posting>> termPostings = new HashMap<>();
        for (String term : finalTerms) {
            List<Posting> postings = invertedIndex.getPostings(term);
            if (postings != null) {
                termPostings.put(term, postings);
            }
        }
        
        Map<Long, Integer> documentLengths = new HashMap<>();
        for (Long id : docIds) {
            documentLengths.put(id, invertedIndex.getDocumentLength(id));
        }
        
        Map<Long, Double> scores = tfIdfScorer.scoreDocuments(finalTerms, termPostings, documentLengths, invertedIndex.getTotalDocuments());
        Map<Long, Map<String, Double>> scoreBreakdowns = tfIdfScorer.getScoreBreakdown(finalTerms, termPostings, documentLengths, invertedIndex.getTotalDocuments());
        List<ScoredDocument> rankedResults = searchResultRanker.rankTopK(scores, limit);
        
        List<SearchResultItem> resultItems = new ArrayList<>();
        for (ScoredDocument sd : rankedResults) {
            long id = sd.docId();
            String title = documentTitles.get(id);
            String content = documentContents.get(id);
            String snippet = generateSnippet(content, finalTerms, 150);
            
            // Collect matched terms for this document
            List<String> matchedTerms = new ArrayList<>();
            for (String term : finalTerms) {
                List<Posting> postings = termPostings.get(term);
                if (postings != null) {
                    for (Posting p : postings) {
                        if (p.docId() == id) {
                            matchedTerms.add(term);
                            break;
                        }
                    }
                }
            }
            
            Map<String, Double> breakdown = scoreBreakdowns.getOrDefault(id, new HashMap<>());
            
            String category = documentCategories.get(id);
            SearchResultItem item = new SearchResultItem(id, title, snippet, sd.score(), matchedTerms, breakdown, category);
            resultItems.add(item);
        }
        
        long endTime = System.nanoTime();
        long executionTimeMs = (endTime - startTime) / 1_000_000;
        
        return new SearchResponse(query, suggestedQuery, scores.size(), executionTimeMs, resultItems, spellCheck);
    }

    public AutocompleteResponse autocomplete(String prefix, int limit) {
        long startTime = System.nanoTime();
        List<String> suggestions = trie.getSuggestions(prefix.toLowerCase(), limit);
        long endTime = System.nanoTime();
        long executionTimeMs = (endTime - startTime) / 1_000_000;
        return new AutocompleteResponse(prefix, suggestions, executionTimeMs);
    }

    public SpellcheckResponse spellcheck(String query) {
        long startTime = System.nanoTime();
        List<String> unstemmedTerms = textProcessorNoStemming.process(query);
        Map<String, List<String>> corrections = spellCorrector.checkQuery(unstemmedTerms);
        String correctedQuery = spellCorrector.correctQuery(unstemmedTerms);
        long endTime = System.nanoTime();
        long executionTimeMs = (endTime - startTime) / 1_000_000;
        return new SpellcheckResponse(query, correctedQuery, corrections, executionTimeMs);
    }
    
    public SearchInspectionDto inspectSearchPerformance(String query) {
        long t0 = System.nanoTime();
        List<String> stemmedTerms = textProcessor.process(query);
        long t1 = System.nanoTime();
        
        Set<Long> docIds = invertedIndex.searchOr(stemmedTerms);
        Map<String, List<Posting>> termPostings = new HashMap<>();
        for (String term : stemmedTerms) {
            List<Posting> postings = invertedIndex.getPostings(term);
            if (postings != null) {
                termPostings.put(term, postings);
            }
        }
        long t2 = System.nanoTime();
        
        Map<Long, Integer> documentLengths = new HashMap<>();
        for (Long id : docIds) {
            documentLengths.put(id, invertedIndex.getDocumentLength(id));
        }
        Map<Long, Double> scores = tfIdfScorer.scoreDocuments(stemmedTerms, termPostings, documentLengths, invertedIndex.getTotalDocuments());
        long t3 = System.nanoTime();
        
        List<ScoredDocument> rankedResults = searchResultRanker.rankTopK(scores, 10);
        long t4 = System.nanoTime();
        
        double tokenizationMs = (t1 - t0) / 1_000_000.0;
        double indexLookupMs = (t2 - t1) / 1_000_000.0;
        double tfIdfScoringMs = (t3 - t2) / 1_000_000.0;
        double rankingMs = (t4 - t3) / 1_000_000.0;
        double totalMs = (t4 - t0) / 1_000_000.0;
        
        return new SearchInspectionDto(query, tokenizationMs, indexLookupMs, tfIdfScoringMs, rankingMs, totalMs, rankedResults.size());
    }

    public void clearIndex() {
        invertedIndex.clear();
        documentTitles.clear();
        documentContents.clear();
    }

    public int getIndexedDocumentCount() {
        return invertedIndex.getTotalDocuments();
    }

    // --- Package-private accessors for VisualizationService ---

    InvertedIndex getInvertedIndex() { return invertedIndex; }
    Trie getTrie() { return trie; }
    TfIdfScorer getTfIdfScorer() { return tfIdfScorer; }
    TextProcessor getTextProcessor() { return textProcessor; }
    TextProcessor getTextProcessorNoStemming() { return textProcessorNoStemming; }
    Map<Long, String> getDocumentTitles() { return documentTitles; }
    Map<Long, String> getDocumentCategories() { return documentCategories; }

    private String generateSnippet(String content, List<String> queryTerms, int snippetLength) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        
        String lowerContent = content.toLowerCase();
        int firstMatchIndex = -1;
        
        for (String term : queryTerms) {
            int idx = lowerContent.indexOf(term.toLowerCase());
            if (idx != -1 && (firstMatchIndex == -1 || idx < firstMatchIndex)) {
                firstMatchIndex = idx;
            }
        }
        
        if (firstMatchIndex == -1) {
            if (content.length() <= snippetLength) {
                return content;
            } else {
                return content.substring(0, snippetLength) + "...";
            }
        }
        
        int start = Math.max(0, firstMatchIndex - snippetLength / 2);
        int end = Math.min(content.length(), start + snippetLength);
        
        String snippet = content.substring(start, end);
        if (start > 0) snippet = "..." + snippet;
        if (end < content.length()) snippet = snippet + "...";
        
        return snippet;
    }
}
