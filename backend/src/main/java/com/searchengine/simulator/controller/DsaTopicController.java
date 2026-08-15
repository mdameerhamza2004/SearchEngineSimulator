package com.searchengine.simulator.controller;

import com.searchengine.simulator.model.LearningTopic;
import com.searchengine.simulator.service.DsaTopicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/topics")
public class DsaTopicController {

    private final DsaTopicService topicService;

    public DsaTopicController(DsaTopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    public ResponseEntity<List<LearningTopic>> getAllTopics() {
        // Return full topics for now; frontend can pick needed fields
        return ResponseEntity.ok(topicService.getAllTopics());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<LearningTopic> getTopic(@PathVariable String slug) {
        return ResponseEntity.ok(topicService.getTopicBySlug(slug));
    }
}
