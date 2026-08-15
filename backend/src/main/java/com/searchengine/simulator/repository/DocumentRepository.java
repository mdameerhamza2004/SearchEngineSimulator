package com.searchengine.simulator.repository;

import com.searchengine.simulator.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByCategory(String category);
    List<Document> findByTitleContainingIgnoreCase(String keyword);
}
