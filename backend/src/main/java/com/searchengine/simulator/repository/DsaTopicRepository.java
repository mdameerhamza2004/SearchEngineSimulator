package com.searchengine.simulator.repository;

import com.searchengine.simulator.model.LearningTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DsaTopicRepository extends JpaRepository<LearningTopic, Long> {
    Optional<LearningTopic> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
