package com.searchengine.simulator.dsa.ranking;

import com.searchengine.simulator.dsa.index.Posting;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Calculates Term Frequency - Inverse Document Frequency (TF-IDF) scores.
 * TF-IDF is used to evaluate how relevant a word is to a document in a collection or corpus.
 *
 * <p>Time Complexity:
 * - scoreDocuments: O(Q * R) where Q is the number of query terms and R is the number of resulting documents.
 * - getScoreBreakdown: O(Q * R)
 *
 * Space Complexity:
 * - O(Q * R) for storing the resulting maps.
 */
public class TfIdfScorer {

    /**
     * Calculates the Term Frequency (TF).
     * Formula: TF = termFrequencyInDoc / totalTermsInDoc
     *
     * @param termFrequencyInDoc Number of times the term appears in the document
     * @param totalTermsInDoc    Total number of terms in the document
     * @return The calculated TF value
     */
    public double calculateTf(int termFrequencyInDoc, int totalTermsInDoc) {
        if (totalTermsInDoc == 0) return 0.0;
        return (double) termFrequencyInDoc / totalTermsInDoc;
    }

    /**
     * Calculates the Inverse Document Frequency (IDF).
     * Formula: IDF = log(totalDocuments / documentsContainingTerm)
     * Handles the edge case where documentsContainingTerm is 0 to avoid division by zero.
     *
     * @param totalDocuments          Total number of documents in the corpus
     * @param documentsContainingTerm Number of documents that contain the term
     * @return The calculated IDF value
     */
    public double calculateIdf(int totalDocuments, int documentsContainingTerm) {
        if (documentsContainingTerm == 0) return 0.0;
        return Math.log((double) totalDocuments / documentsContainingTerm);
    }

    /**
     * Calculates the TF-IDF score for a specific term in a specific document.
     * Formula: TF-IDF = TF * IDF
     *
     * @param termFrequencyInDoc      Number of times the term appears in the doc
     * @param totalTermsInDoc         Total number of terms in the doc
     * @param totalDocuments          Total number of documents in the corpus
     * @param documentsContainingTerm Number of documents that contain the term
     * @return The calculated TF-IDF score
     */
    public double calculateTfIdf(int termFrequencyInDoc, int totalTermsInDoc, int totalDocuments, int documentsContainingTerm) {
        double tf = calculateTf(termFrequencyInDoc, totalTermsInDoc);
        double idf = calculateIdf(totalDocuments, documentsContainingTerm);
        return tf * idf;
    }

    /**
     * Scores documents based on multiple query terms using TF-IDF.
     *
     * @param queryTerms      List of search terms
     * @param termPostings    Map of terms to their postings list
     * @param documentLengths Map of document IDs to their total word count
     * @param totalDocuments  Total number of documents in the corpus
     * @return Map of Document ID to their total TF-IDF score
     */
    public Map<Long, Double> scoreDocuments(List<String> queryTerms, Map<String, List<Posting>> termPostings, Map<Long, Integer> documentLengths, int totalDocuments) {
        Map<Long, Double> documentScores = new HashMap<>();

        for (String term : queryTerms) {
            List<Posting> postings = termPostings.get(term);
            if (postings == null || postings.isEmpty()) {
                continue;
            }

            int df = postings.size();
            double idf = calculateIdf(totalDocuments, df);

            for (Posting posting : postings) {
                long docId = posting.docId();
                int tfInDoc = posting.termFrequency();
                int docLength = documentLengths.getOrDefault(docId, 1);

                double tf = calculateTf(tfInDoc, docLength);
                double tfIdf = tf * idf;

                documentScores.put(docId, documentScores.getOrDefault(docId, 0.0) + tfIdf);
            }
        }

        return documentScores;
    }

    /**
     * Provides a detailed breakdown of TF-IDF scores per term for each document.
     * Useful for score explanation UI.
     *
     * @param queryTerms      List of search terms
     * @param termPostings    Map of terms to their postings list
     * @param documentLengths Map of document IDs to their total word count
     * @param totalDocuments  Total number of documents in the corpus
     * @return Map of Document ID -> (Term -> Score)
     */
    public Map<Long, Map<String, Double>> getScoreBreakdown(List<String> queryTerms, Map<String, List<Posting>> termPostings, Map<Long, Integer> documentLengths, int totalDocuments) {
        Map<Long, Map<String, Double>> breakdown = new HashMap<>();

        for (String term : queryTerms) {
            List<Posting> postings = termPostings.get(term);
            if (postings == null || postings.isEmpty()) {
                continue;
            }

            int df = postings.size();
            double idf = calculateIdf(totalDocuments, df);

            for (Posting posting : postings) {
                long docId = posting.docId();
                int tfInDoc = posting.termFrequency();
                int docLength = documentLengths.getOrDefault(docId, 1);

                double tf = calculateTf(tfInDoc, docLength);
                double tfIdf = tf * idf;

                breakdown.computeIfAbsent(docId, k -> new HashMap<>()).put(term, tfIdf);
            }
        }

        return breakdown;
    }
}
