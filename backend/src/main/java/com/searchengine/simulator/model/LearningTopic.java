package com.searchengine.simulator.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "learning_topics")
public class LearningTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String difficulty;

    @Column(columnDefinition = "TEXT")
    private String overview;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonListConverter.class)
    private List<Object> coreConcepts;

    @Column(columnDefinition = "TEXT")
    private String visualExplanation;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonListConverter.class)
    private List<Object> learningObjectives;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(columnDefinition = "TEXT")
    private String internalWorking;

    @Column(columnDefinition = "TEXT")
    private String algorithm;

    @Column(columnDefinition = "TEXT")
    private String pseudocode;

    private String timeComplexity;
    private String spaceComplexity;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonListConverter.class)
    private List<Object> realWorldApplications;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonListConverter.class)
    private List<Object> relatedTopics;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonMapConverter.class)
    private Map<String, Object> codeImplementations;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonListConverter.class)
    private List<Object> practiceQuestions;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonListConverter.class)
    private List<Object> interviewQuestions;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonListConverter.class)
    private List<Object> mcqs;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonListConverter.class)
    private List<Object> outputBasedQuestions;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonListConverter.class)
    private List<Object> commonMistakes;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonListConverter.class)
    private List<Object> importantPatterns;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonListConverter.class)
    private List<Object> deepDiveInterviewQuestions;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonListConverter.class)
    private List<Object> comparisonQuestions;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonListConverter.class)
    private List<Object> conceptualQuestions;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonListConverter.class)
    private List<Object> edgeCases;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonListConverter.class)
    private List<Object> placementQuestions;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }

    public List<Object> getCoreConcepts() { return coreConcepts; }
    public void setCoreConcepts(List<Object> coreConcepts) { this.coreConcepts = coreConcepts; }

    public String getVisualExplanation() { return visualExplanation; }
    public void setVisualExplanation(String visualExplanation) { this.visualExplanation = visualExplanation; }

    public List<Object> getLearningObjectives() { return learningObjectives; }
    public void setLearningObjectives(List<Object> learningObjectives) { this.learningObjectives = learningObjectives; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getInternalWorking() { return internalWorking; }
    public void setInternalWorking(String internalWorking) { this.internalWorking = internalWorking; }

    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }

    public String getPseudocode() { return pseudocode; }
    public void setPseudocode(String pseudocode) { this.pseudocode = pseudocode; }

    public String getTimeComplexity() { return timeComplexity; }
    public void setTimeComplexity(String timeComplexity) { this.timeComplexity = timeComplexity; }

    public String getSpaceComplexity() { return spaceComplexity; }
    public void setSpaceComplexity(String spaceComplexity) { this.spaceComplexity = spaceComplexity; }

    public List<Object> getRealWorldApplications() { return realWorldApplications; }
    public void setRealWorldApplications(List<Object> realWorldApplications) { this.realWorldApplications = realWorldApplications; }

    public List<Object> getRelatedTopics() { return relatedTopics; }
    public void setRelatedTopics(List<Object> relatedTopics) { this.relatedTopics = relatedTopics; }

    public Map<String, Object> getCodeImplementations() { return codeImplementations; }
    public void setCodeImplementations(Map<String, Object> codeImplementations) { this.codeImplementations = codeImplementations; }

    public List<Object> getPracticeQuestions() { return practiceQuestions; }
    public void setPracticeQuestions(List<Object> practiceQuestions) { this.practiceQuestions = practiceQuestions; }

    public List<Object> getInterviewQuestions() { return interviewQuestions; }
    public void setInterviewQuestions(List<Object> interviewQuestions) { this.interviewQuestions = interviewQuestions; }

    public List<Object> getMcqs() { return mcqs; }
    public void setMcqs(List<Object> mcqs) { this.mcqs = mcqs; }

    public List<Object> getOutputBasedQuestions() { return outputBasedQuestions; }
    public void setOutputBasedQuestions(List<Object> outputBasedQuestions) { this.outputBasedQuestions = outputBasedQuestions; }

    public List<Object> getCommonMistakes() { return commonMistakes; }
    public void setCommonMistakes(List<Object> commonMistakes) { this.commonMistakes = commonMistakes; }

    public List<Object> getImportantPatterns() { return importantPatterns; }
    public void setImportantPatterns(List<Object> importantPatterns) { this.importantPatterns = importantPatterns; }

    public List<Object> getDeepDiveInterviewQuestions() { return deepDiveInterviewQuestions; }
    public void setDeepDiveInterviewQuestions(List<Object> deepDiveInterviewQuestions) { this.deepDiveInterviewQuestions = deepDiveInterviewQuestions; }

    public List<Object> getComparisonQuestions() { return comparisonQuestions; }
    public void setComparisonQuestions(List<Object> comparisonQuestions) { this.comparisonQuestions = comparisonQuestions; }

    public List<Object> getConceptualQuestions() { return conceptualQuestions; }
    public void setConceptualQuestions(List<Object> conceptualQuestions) { this.conceptualQuestions = conceptualQuestions; }

    public List<Object> getEdgeCases() { return edgeCases; }
    public void setEdgeCases(List<Object> edgeCases) { this.edgeCases = edgeCases; }

    public List<Object> getPlacementQuestions() { return placementQuestions; }
    public void setPlacementQuestions(List<Object> placementQuestions) { this.placementQuestions = placementQuestions; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
