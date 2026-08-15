package com.searchengine.simulator.controller;

import com.searchengine.simulator.model.dto.DocumentRequest;
import com.searchengine.simulator.model.dto.DocumentResponse;
import com.searchengine.simulator.model.dto.DocumentStatsDto;
import com.searchengine.simulator.service.DocumentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Adds a new document to the index and database.
     *
     * @param request The document creation request.
     * @return The created document response.
     */
    @PostMapping
    public ResponseEntity<DocumentResponse> addDocument(@Valid @RequestBody DocumentRequest request) {
        log.info("Adding new document with title: {}", request.title());
        DocumentResponse response = documentService.addDocument(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves all documents.
     *
     * @return A list of all documents.
     */
    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getAllDocuments() {
        log.info("Retrieving all documents");
        List<DocumentResponse> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }

    /**
     * Retrieves a single document by its ID.
     *
     * @param id The ID of the document to retrieve.
     * @return The document response.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocument(@PathVariable Long id) {
        log.info("Retrieving document with id: {}", id);
        DocumentResponse response = documentService.getDocument(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/stats")
    public ResponseEntity<DocumentStatsDto> getDocumentStats(@PathVariable Long id) {
        log.info("Retrieving stats for document with id: {}", id);
        DocumentStatsDto response = documentService.getDocumentStats(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates an existing document.
     *
     * @param id      The ID of the document to update.
     * @param request The document update request.
     * @return The updated document response.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DocumentResponse> updateDocument(
            @PathVariable Long id,
            @Valid @RequestBody DocumentRequest request) {
        log.info("Updating document with id: {}", id);
        DocumentResponse response = documentService.updateDocument(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a document by its ID.
     *
     * @param id The ID of the document to delete.
     * @return A 204 No Content response.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        log.info("Deleting document with id: {}", id);
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
