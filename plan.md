"# DSA-Based Search Engine Simulator — Implementation Plan

A portfolio-grade search engine simulator that demonstrates mastery of core Data Structures & Algorithms through a polished, full-stack application. Every search operation — indexing, autocomplete, ranking, and spell correction — is powered by hand-written DSA implementations, **not** external search libraries.

---

## 1. Architecture Overview

```mermaid
graph TB
    subgraph Frontend [\"Frontend — React + Vite\"]
        UI[\"Search UI<br/>(Google-inspired)\"]
        Admin[\"Admin Panel\"]
        Viz[\"DSA Visualizations\"]
        Analytics[\"Analytics Dashboard\"]
    end

    subgraph API [\"REST API Layer\"]
        SC[\"SearchController\"]
        DC[\"DocumentController\"]
        AC[\"AnalyticsController\"]
        VC[\"VisualizationController\"]
    end

    subgraph Services [\"Service Layer\"]
        SS[\"SearchService\"]
        DS[\"DocumentService\"]
        IS[\"IndexingService\"]
        AS[\"AnalyticsService\"]
    end

    subgraph DSA [\"Core DSA Engine (No External Libraries)\"]
        II[\"Inverted Index<br/>(HashMap)\"]
        TR[\"Trie<br/>(Autocomplete)\"]
        TFIDF[\"TF-IDF Scorer\"]
        PQ[\"Priority Queue<br/>(Top-K)\"]
        SC2[\"Spell Corrector<br/>(Levenshtein)\"]
        TP[\"Text Processor<br/>(Tokenizer + Stemmer)\"]
    end

    subgraph Storage [\"Persistence\"]
        H2[\"H2 / In-Memory Store<br/>(Document Storage)\"]
        JSON[\"Sample Dataset<br/>(JSON files)\"]
    end

    UI --> SC
    Admin --> DC
    Analytics --> AC
    Viz --> VC

    SC --> SS
    DC --> DS
    DC --> IS
    AC --> AS

    SS --> II
    SS --> TR
    SS --> TFIDF
    SS --> PQ
    SS --> SC2
    IS --> TP
    IS --> II
    IS --> TR

    DS --> H2
    IS --> H2
    JSON --> IS
```

### Key Architectural Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Backend framework | Spring Boot 3.4.x + Java 21 | Industry
<truncated 26488 bytes>