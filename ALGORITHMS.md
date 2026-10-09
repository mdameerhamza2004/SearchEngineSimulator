# Core Data Structures & Algorithms

This document outlines the core algorithms implemented from scratch in `backend/src/main/java/com/searchengine/simulator/dsa/` and their respective complexities. External search libraries (Elasticsearch, Lucene) were strictly avoided to demonstrate fundamental computer science concepts.

## 1. Inverted Index (`InvertedIndex.java`)
The Inverted Index maps individual words (terms) to the list of documents they appear in, alongside their term frequencies and specific positions.

- **Data Structure**: `ConcurrentHashMap<String, List<Posting>>`
- **Why**: Allows O(1) average time complexity for term lookups, making it the fastest way to retrieve all documents containing a specific search keyword.
- **Complexity**:
  - Indexing a document of length `L`: **O(L)**
  - Searching for a single term: **O(1)**
  - Boolean AND/OR operations on `k` terms: **O(k * P)** where `P` is the max postings list length.

## 2. Trie / Prefix Tree (`Trie.java`)
The Trie is used for lightning-fast autocomplete and prefix suggestions.

- **Data Structure**: Tree of `TrieNode` objects, where each node contains a `HashMap<Character, TrieNode>`.
- **Why**: Standard HashMaps cannot efficiently perform prefix matching (e.g., finding all words starting with "algo"). A Trie allows prefix searches in time proportional to the prefix length, regardless of the dictionary size.
- **Complexity**:
  - Inserting a word of length `W`: **O(W)**
  - Checking if a word exists: **O(W)**
  - Finding all suggestions for prefix `P`: **O(P + V)** where `V` is the number of nodes in the subtree below the prefix.

## 3. Top-K Ranking / Priority Queue (`SearchResultRanker.java`)
Retrieves the most relevant search results out of thousands of matches without sorting the entire array.

- **Data Structure**: Min-Heap (`java.util.PriorityQueue`).
- **Why**: Sorting all `N` matching documents takes `O(N log N)`. Maintaining a Min-Heap of size `K` (where K is the number of results we want to display, e.g., 10) reduces this to `O(N log K)`.
- **Complexity**:
  - Ranking `N` documents to find top `K`: **O(N log K)**
  - Space Complexity: **O(K)** for the heap.

## 4. TF-IDF Scoring (`TfIdfScorer.java`)
Term Frequency - Inverse Document Frequency calculates the relevance of a document to a query.

- **Formula**: `TF * IDF`
  - **TF**: `(Frequency of term in document) / (Total words in document)`
  - **IDF**: `log(Total documents in index / Documents containing the term)`
- **Why**: Simple term frequency biases long documents. TF-IDF heavily weights rare, highly specific terms while penalizing common words (like "the", "and") that don't add semantic value.
- **Complexity**: **O(T)** per document scored, where `T` is the number of unique terms in the query.

## 5. Spell Correction (`LevenshteinDistance.java`)
Suggests "Did you mean...?" corrections for misspelled queries.

- **Algorithm**: Levenshtein Edit Distance (Dynamic Programming approach).
- **Why**: Calculates the minimum number of single-character edits (insertions, deletions, or substitutions) required to change one word into another.
- **Complexity**:
  - Comparing two words of lengths `A` and `B`: **O(A * B)** Time and **O(B)** Space (optimized from `O(A * B)` space by using two 1D arrays).
  - Searching dictionary of `D` words: **O(D * A * B)**. 

## 6. Text Processing Pipeline (`TextProcessor.java`, `PorterStemmer.java`)
Normalizes text before it is indexed or searched.

- **Steps**:
  1. **Tokenization**: Splits text into alphanumeric tokens (`O(N)`).
  2. **Case Normalization**: Converts all to lowercase.
  3. **Stop Word Removal**: Filters out ~175 common English words using an `O(1)` HashSet lookup.
  4. **Stemming**: Implements the classic Porter Stemmer algorithm to reduce words to their root form (e.g., "running" → "run").
- **Complexity**: Overall pipeline runs in **O(N)** where `N` is the character length of the text.
