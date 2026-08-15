package com.searchengine.simulator.model.dto;

import com.searchengine.simulator.model.Document;
import java.time.LocalDateTime;

public record DocumentResponse(
        Long id,
        String title,
        String content,
        String category,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static DocumentResponse from(Document doc) {
        return new DocumentResponse(
                doc.getId(),
                doc.getTitle(),
                doc.getContent(),
                doc.getCategory(),
                doc.getCreatedAt(),
                doc.getUpdatedAt()
        );
    }
}
