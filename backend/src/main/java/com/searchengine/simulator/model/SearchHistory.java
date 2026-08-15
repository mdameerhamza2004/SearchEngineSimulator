package com.searchengine.simulator.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "search_history")
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String query;

    @Column(nullable = false)
    private int resultCount;

    @Column(nullable = false)
    private long executionTimeMs;

    @Column(nullable = false)
    private boolean zeroResults;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public SearchHistory() {}

    public SearchHistory(String query, int resultCount, long executionTimeMs) {
        this.query = query;
        this.resultCount = resultCount;
        this.executionTimeMs = executionTimeMs;
        this.zeroResults = (resultCount == 0);
    }

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public String getQuery() { return query; }
    public int getResultCount() { return resultCount; }
    public long getExecutionTimeMs() { return executionTimeMs; }
    public boolean isZeroResults() { return zeroResults; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
