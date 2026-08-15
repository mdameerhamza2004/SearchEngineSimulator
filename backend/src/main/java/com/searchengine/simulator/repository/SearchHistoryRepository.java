package com.searchengine.simulator.repository;

import com.searchengine.simulator.model.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    @Query("SELECT sh.query as query, COUNT(sh) as searchCount, AVG(sh.resultCount) as avgResults, AVG(sh.executionTimeMs) as avgLatency " +
           "FROM SearchHistory sh GROUP BY sh.query ORDER BY searchCount DESC, avgLatency ASC LIMIT 10")
    List<Object[]> findTopQueries();
    
    @Query("SELECT COUNT(sh) FROM SearchHistory sh WHERE sh.zeroResults = true")
    long countZeroResultSearches();
    
    @Query("SELECT AVG(sh.executionTimeMs) FROM SearchHistory sh")
    Double getAverageLatency();

    @Query("SELECT AVG(sh.resultCount) FROM SearchHistory sh")
    Double getAverageResults();
    
    List<SearchHistory> findTop20ByOrderByTimestampDesc();
}
