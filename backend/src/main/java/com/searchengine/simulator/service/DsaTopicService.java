package com.searchengine.simulator.service;

import com.searchengine.simulator.model.LearningTopic;
import com.searchengine.simulator.repository.DsaTopicRepository;
import com.searchengine.simulator.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DsaTopicService {
    private static final Logger log = LoggerFactory.getLogger(DsaTopicService.class);
    
    private final DsaTopicRepository repository;
    private final SearchEngineService searchEngineService;

    public DsaTopicService(DsaTopicRepository repository, SearchEngineService searchEngineService) {
        this.repository = repository;
        this.searchEngineService = searchEngineService;
    }

    public List<LearningTopic> getAllTopics() {
        return repository.findAll();
    }

    public LearningTopic getTopicBySlug(String slug) {
        return repository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found: " + slug));
    }

    @Transactional
    public LearningTopic saveTopic(LearningTopic topic) {
        boolean isNew = topic.getId() == null;
        LearningTopic savedTopic = repository.save(topic);
        
        // Index into search engine
        indexTopic(savedTopic);
        
        return savedTopic;
    }
    
    private void indexTopic(LearningTopic topic) {
        String indexContent = String.join(" ", 
            topic.getTitle(),
            topic.getOverview() != null ? topic.getOverview() : "",
            topic.getExplanation() != null ? topic.getExplanation() : "",
            topic.getAlgorithm() != null ? topic.getAlgorithm() : "",
            topic.getInternalWorking() != null ? topic.getInternalWorking() : ""
        );
        
        // We use a negative ID space for topics to avoid clashing with normal documents,
        // or we just rely on ID generation. Since Document and Topic are different tables,
        // their IDs might collide in the inverted index if we just pass id.
        // For simplicity, let's offset topic IDs by 1000000.
        long docId = topic.getId() + 1000000L; 
        searchEngineService.indexDocument(docId, "Topic: " + topic.getTitle(), indexContent, "LEARNING_TOPIC");
    }

    @Transactional
    public void deleteTopic(String slug) {
        LearningTopic topic = getTopicBySlug(slug);
        repository.delete(topic);
        searchEngineService.removeDocument(topic.getId() + 1000000L);
    }
}
