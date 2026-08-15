package com.searchengine.simulator.service;

import com.searchengine.simulator.dsa.index.InvertedIndex;
import com.searchengine.simulator.dsa.index.Posting;
import com.searchengine.simulator.dsa.ranking.TfIdfScorer;
import com.searchengine.simulator.dsa.spellcheck.LevenshteinDistance;
import com.searchengine.simulator.dsa.text.TextProcessor;
import com.searchengine.simulator.dsa.trie.Trie;
import com.searchengine.simulator.dsa.trie.TrieNode;
import com.searchengine.simulator.model.dto.*;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Read-only service that exposes internal DSA algorithm state for visualization.
 * Delegates exclusively to the existing DSA instances in SearchEngineService.
 * Does NOT duplicate any algorithm logic.
 */
@Service
public class VisualizationService {

    private final SearchEngineService searchEngineService;
    private final LevenshteinDistance levenshtein = new LevenshteinDistance();

    public VisualizationService(SearchEngineService searchEngineService) {
        this.searchEngineService = searchEngineService;
    }

    // -------------------------------------------------------------------------
    // TRIE VISUALIZATION
    // -------------------------------------------------------------------------

    /**
     * Returns the Trie subtree for a given prefix as a flat list of nodes and edges,
     * along with the traversal path and autocomplete suggestions.
     */
    public TrieVisualizationResponse getTrieVisualization(String prefix) {
        Trie trie = searchEngineService.getTrie();
        String normalizedPrefix = (prefix == null ? "" : prefix.toLowerCase().trim());

        List<TrieVisualizationResponse.TrieNodeDto> nodes = new ArrayList<>();
        List<TrieVisualizationResponse.TrieEdgeDto> edges = new ArrayList<>();
        Set<String> traversalIds = new HashSet<>();

        // Build traversal path IDs
        TrieNode root = trie.getRoot();
        TrieNode current = root;
        String currentId = "ROOT";
        traversalIds.add(currentId);
        boolean traversalBroken = false;

        for (char c : normalizedPrefix.toCharArray()) {
            if (!current.hasChild(c)) {
                traversalBroken = true;
                break;
            }
            String nextId = currentId + "-" + c;
            traversalIds.add(nextId);
            current = current.getChild(c);
            currentId = nextId;
        }

        // BFS from root to build nodes/edges (limit depth to keep visualization manageable)
        buildTrieNodes(root, "ROOT", 0, 5, nodes, edges, traversalIds, trie);

        List<String> suggestions = traversalBroken ? List.of()
                : trie.getSuggestions(normalizedPrefix, 8);

        List<String> traversalPathList = new ArrayList<>(traversalIds);

        return new TrieVisualizationResponse(
                normalizedPrefix,
                nodes,
                edges,
                traversalPathList,
                suggestions,
                trie.getSize()
        );
    }

    private void buildTrieNodes(TrieNode node, String id, int depth, int maxDepth,
                                 List<TrieVisualizationResponse.TrieNodeDto> nodes,
                                 List<TrieVisualizationResponse.TrieEdgeDto> edges,
                                 Set<String> traversalIds,
                                 Trie trie) {
        if (depth > maxDepth) return;

        boolean inPath = traversalIds.contains(id);
        boolean isSuggestion = node.isEndOfWord() && depth > 0;

        String character = id.equals("ROOT") ? "ROOT"
                : String.valueOf(id.charAt(id.length() - 1));

        nodes.add(new TrieVisualizationResponse.TrieNodeDto(
                id, character, node.isEndOfWord(), node.getFrequency(),
                node.getWord(), depth, inPath, isSuggestion
        ));

        for (Map.Entry<Character, TrieNode> entry : node.getChildren().entrySet()) {
            char c = entry.getKey();
            TrieNode child = entry.getValue();
            String childId = id + "-" + c;
            boolean edgeInPath = traversalIds.contains(id) && traversalIds.contains(childId);

            edges.add(new TrieVisualizationResponse.TrieEdgeDto(id, childId, String.valueOf(c), edgeInPath));
            buildTrieNodes(child, childId, depth + 1, maxDepth, nodes, edges, traversalIds, trie);
        }
    }

    // -------------------------------------------------------------------------
    // INVERTED INDEX VISUALIZATION
    // -------------------------------------------------------------------------

    /**
     * Looks up a term in the inverted index and returns real posting data
     * enriched with document title and category.
     */
    public InvertedIndexVisualizationResponse getInvertedIndexVisualization(String term) {
        if (term == null || term.isBlank()) {
            return new InvertedIndexVisualizationResponse(term, term, 0,
                    searchEngineService.getInvertedIndex().getTotalDocuments(), List.of());
        }

        TextProcessor textProcessor = searchEngineService.getTextProcessor();
        InvertedIndex index = searchEngineService.getInvertedIndex();

        // Process the term the same way the search engine does
        List<String> processed = textProcessor.process(term);
        String stemmedTerm = processed.isEmpty() ? term.toLowerCase().trim() : processed.get(0);

        List<Posting> postings = index.getPostings(stemmedTerm);
        Map<Long, String> titles = searchEngineService.getDocumentTitles();
        Map<Long, String> categories = searchEngineService.getDocumentCategories();
        TfIdfScorer scorer = searchEngineService.getTfIdfScorer();

        List<InvertedIndexVisualizationResponse.PostingDto> postingDtos = postings.stream()
                .map(p -> {
                    int docLen = index.getDocumentLength(p.docId());
                    double tf = scorer.calculateTf(p.termFrequency(), docLen);
                    return new InvertedIndexVisualizationResponse.PostingDto(
                            p.docId(),
                            titles.getOrDefault(p.docId(), "Unknown"),
                            categories.getOrDefault(p.docId(), ""),
                            p.termFrequency(),
                            docLen,
                            tf,
                            p.positions()
                    );
                })
                .sorted(Comparator.comparingDouble(InvertedIndexVisualizationResponse.PostingDto::tf).reversed())
                .toList();

        return new InvertedIndexVisualizationResponse(
                stemmedTerm,
                term.toLowerCase().trim(),
                postings.size(),
                index.getTotalDocuments(),
                postingDtos
        );
    }

    // -------------------------------------------------------------------------
    // TF-IDF VISUALIZATION
    // -------------------------------------------------------------------------

    /**
     * Returns the full TF-IDF breakdown for a query, showing every calculation step.
     * Values are identical to those used by the actual search engine.
     */
    public TfIdfVisualizationResponse getTfIdfVisualization(String query) {
        if (query == null || query.isBlank()) {
            return new TfIdfVisualizationResponse(query, List.of(), List.of(), 0, List.of(), List.of());
        }

        TextProcessor tp = searchEngineService.getTextProcessor();
        TextProcessor tpRaw = searchEngineService.getTextProcessorNoStemming();
        InvertedIndex index = searchEngineService.getInvertedIndex();
        TfIdfScorer scorer = searchEngineService.getTfIdfScorer();
        Map<Long, String> titles = searchEngineService.getDocumentTitles();
        Map<Long, String> categories = searchEngineService.getDocumentCategories();

        List<String> rawTokens = tpRaw.process(query);
        List<String> processedTerms = tp.process(query);
        int totalDocs = index.getTotalDocuments();

        // Per-term stats
        List<TfIdfVisualizationResponse.TermStats> termStats = new ArrayList<>();
        Map<String, List<Posting>> termPostingsMap = new LinkedHashMap<>();
        for (String term : processedTerms) {
            List<Posting> postings = index.getPostings(term);
            termPostingsMap.put(term, postings);
            double idf = scorer.calculateIdf(totalDocs, postings.size());
            termStats.add(new TfIdfVisualizationResponse.TermStats(term, postings.size(), idf));
        }

        // Collect all matching doc IDs
        Set<Long> docIds = new LinkedHashSet<>();
        for (List<Posting> postings : termPostingsMap.values()) {
            for (Posting p : postings) docIds.add(p.docId());
        }

        // Per-document breakdown — same calculation as TfIdfScorer.scoreDocuments
        Map<Long, Double> totalScores = new HashMap<>();
        Map<Long, List<TfIdfVisualizationResponse.TermDocScore>> perDocBreakdown = new LinkedHashMap<>();

        for (String term : processedTerms) {
            List<Posting> postings = termPostingsMap.get(term);
            double idf = scorer.calculateIdf(totalDocs, postings.size());
            for (Posting p : postings) {
                int docLen = index.getDocumentLength(p.docId());
                double tf = scorer.calculateTf(p.termFrequency(), docLen);
                double tfIdf = tf * idf;
                totalScores.merge(p.docId(), tfIdf, Double::sum);
                perDocBreakdown.computeIfAbsent(p.docId(), k -> new ArrayList<>())
                        .add(new TfIdfVisualizationResponse.TermDocScore(
                                term, p.termFrequency(), docLen, tf, idf, tfIdf));
            }
        }

        List<TfIdfVisualizationResponse.DocumentScore> documentScores = totalScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(e -> new TfIdfVisualizationResponse.DocumentScore(
                        e.getKey(),
                        titles.getOrDefault(e.getKey(), "Unknown"),
                        categories.getOrDefault(e.getKey(), ""),
                        e.getValue(),
                        perDocBreakdown.getOrDefault(e.getKey(), List.of())
                ))
                .toList();

        return new TfIdfVisualizationResponse(query, rawTokens, processedTerms,
                totalDocs, termStats, documentScores);
    }

    // -------------------------------------------------------------------------
    // LEVENSHTEIN VISUALIZATION
    // -------------------------------------------------------------------------

    /**
     * Computes the Levenshtein DP matrix and operation list using the real LevenshteinDistance class.
     */
    public LevenshteinVisualizationResponse getLevenshteinVisualization(String source, String target) {
        String s = (source == null ? "" : source.trim().toLowerCase());
        String t = (target == null ? "" : target.trim().toLowerCase());

        // Enforce reasonable limits to avoid huge matrices
        if (s.length() > 20) s = s.substring(0, 20);
        if (t.length() > 20) t = t.substring(0, 20);

        int[][] matrix = levenshtein.getDistanceMatrix(s, t);
        List<String> operations = levenshtein.getOperations(s, t);
        int distance = s.isEmpty() && t.isEmpty() ? 0 : matrix[s.length()][t.length()];

        return new LevenshteinVisualizationResponse(s, t, distance, matrix, operations);
    }

    // -------------------------------------------------------------------------
    // TEXT PROCESSING PIPELINE VISUALIZATION
    // -------------------------------------------------------------------------

    /**
     * Runs text through each stage of the TextProcessor pipeline and returns
     * the tokens at each stage. Uses the real TextProcessor instances.
     */
    public TextPipelineVisualizationResponse getTextPipelineVisualization(String text) {
        if (text == null || text.isBlank()) {
            return new TextPipelineVisualizationResponse(text, List.of(), List.of(),
                    List.of(), List.of(), List.of());
        }

        TextProcessor fullProcessor = searchEngineService.getTextProcessor();
        TextProcessor noStemmingProcessor = searchEngineService.getTextProcessorNoStemming();

        List<String> afterTokenization = fullProcessor.tokenizeOnly(text);
        List<String> afterStopWordRemoval = noStemmingProcessor.process(text);  // stop words filtered, no stemming
        List<String> afterStemming = fullProcessor.process(text);

        // Find removed stop words
        Set<String> tokenSet = new HashSet<>(afterTokenization);
        Set<String> filteredSet = new HashSet<>(afterStopWordRemoval);
        List<String> removedStopWords = afterTokenization.stream()
                .filter(w -> !filteredSet.contains(w))
                .distinct()
                .toList();

        // Build stem change pairs
        List<TextPipelineVisualizationResponse.StemChange> stemChanges = new ArrayList<>();
        int minLen = Math.min(afterStopWordRemoval.size(), afterStemming.size());
        for (int i = 0; i < minLen; i++) {
            String orig = afterStopWordRemoval.get(i);
            String stemmed = afterStemming.get(i);
            stemChanges.add(new TextPipelineVisualizationResponse.StemChange(orig, stemmed, !orig.equals(stemmed)));
        }

        return new TextPipelineVisualizationResponse(
                text, afterTokenization, afterStopWordRemoval,
                afterStemming, removedStopWords, stemChanges
        );
    }
}
