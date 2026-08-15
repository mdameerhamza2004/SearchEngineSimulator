package com.searchengine.simulator.service;

import com.searchengine.simulator.model.Document;
import com.searchengine.simulator.model.dto.DocumentRequest;
import com.searchengine.simulator.model.dto.DocumentResponse;
import com.searchengine.simulator.model.dto.DocumentStatsDto;
import com.searchengine.simulator.repository.DocumentRepository;
import com.searchengine.simulator.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);
    private final DocumentRepository documentRepository;
    private final SearchEngineService searchEngineService;

    public DocumentService(DocumentRepository documentRepository, SearchEngineService searchEngineService) {
        this.documentRepository = documentRepository;
        this.searchEngineService = searchEngineService;
    }

    @Transactional
    public DocumentResponse addDocument(DocumentRequest request) {
        Document document = new Document();
        document.setTitle(request.title());
        document.setContent(request.content());
        document.setCategory(request.category());
        
        document = documentRepository.save(document);
        
        searchEngineService.indexDocument(document.getId(), document.getTitle(), document.getContent(), document.getCategory());
        
        return DocumentResponse.from(document);
    }

    public DocumentResponse getDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));
        return DocumentResponse.from(document);
    }

    public List<DocumentResponse> getAllDocuments() {
        return documentRepository.findAll().stream()
                .map(DocumentResponse::from)
                .collect(Collectors.toList());
    }

    public DocumentStatsDto getDocumentStats(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));
        
        List<String> tokens = searchEngineService.getTextProcessor().process(document.getContent());
        int tokenCount = tokens.size();
        
        java.util.Map<String, Integer> freqMap = new java.util.HashMap<>();
        for (String t : tokens) {
            freqMap.put(t, freqMap.getOrDefault(t, 0) + 1);
        }
        
        int uniqueTerms = freqMap.size();
        List<java.util.Map.Entry<String, Integer>> topTerms = freqMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .collect(Collectors.toList());
                
        return new DocumentStatsDto(id, tokenCount, uniqueTerms, topTerms);
    }

    @Transactional
    public DocumentResponse updateDocument(Long id, DocumentRequest request) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));
        
        document.setTitle(request.title());
        document.setContent(request.content());
        document.setCategory(request.category());
        
        document = documentRepository.save(document);
        
        searchEngineService.removeDocument(id);
        searchEngineService.indexDocument(document.getId(), document.getTitle(), document.getContent(), document.getCategory());
        
        return DocumentResponse.from(document);
    }

    @Transactional
    public void deleteDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));
                
        documentRepository.delete(document);
        searchEngineService.removeDocument(id);
    }

    @Transactional(readOnly = true)
    public void reindexAll() {
        searchEngineService.clearIndex();
        List<Document> documents = documentRepository.findAll();
        for (Document document : documents) {
            searchEngineService.indexDocument(document.getId(), document.getTitle(), document.getContent(), document.getCategory());
        }
        log.info("Reindexed {} documents.", documents.size());
    }

    public long getDocumentCount() {
        return documentRepository.count();
    }
}
