import React, { useState } from 'react';
import { GitBranch, List, BarChart2, Layers, Spline, AlignLeft } from 'lucide-react';
import TrieVisualizer from '../components/visualizers/TrieVisualizer';
import InvertedIndexVisualizer from '../components/visualizers/InvertedIndexVisualizer';
import TfIdfVisualizer from '../components/visualizers/TfIdfVisualizer';
import TopKVisualizer from '../components/visualizers/TopKVisualizer';
import LevenshteinVisualizer from '../components/visualizers/LevenshteinVisualizer';
import TextPipelineVisualizer from '../components/visualizers/TextPipelineVisualizer';

const ALGORITHMS = [
  {
    id: 'trie',
    icon: <GitBranch size={22} />,
    name: 'Trie / Autocomplete',
    tagline: 'Prefix-tree for O(m) autocomplete',
    purpose: 'Stores vocabulary words in a tree where each node is a character. Enables instant prefix lookups for autocomplete suggestions.',
    usage: 'Every keystroke in the search bar triggers a Trie.getSuggestions(prefix, limit) lookup.',
    timeComplexity: 'O(m + k) — m = prefix length, k = suggestions collected',
    spaceComplexity: 'O(n × m) — n words, average length m',
    component: <TrieVisualizer />,
    examples: ['pro', 'bin', 'alg', 'sort', 'gra'],
  },
  {
    id: 'inverted-index',
    icon: <List size={22} />,
    name: 'Inverted Index',
    tagline: 'HashMap: term → [doc, freq, positions]',
    purpose: 'Maps every unique term to the list of documents it appears in, along with term frequency and positions. The backbone of full-text search.',
    usage: 'Every search query runs InvertedIndex.searchOr(terms) to find candidate documents.',
    timeComplexity: 'O(1) per term lookup via HashMap; O(∑df) for multi-term OR',
    spaceComplexity: 'O(n × v) — n documents, v unique terms',
    component: <InvertedIndexVisualizer />,
    examples: ['algorithm', 'tree', 'hash', 'sort', 'graph'],
  },
  {
    id: 'tfidf',
    icon: <BarChart2 size={22} />,
    name: 'TF-IDF Ranking',
    tagline: 'Term Frequency × Inverse Document Frequency',
    purpose: 'Scores documents by how relevant they are to a query. Terms rare across documents (high IDF) and frequent in a document (high TF) give higher scores.',
    usage: 'After candidate retrieval, TfIdfScorer.scoreDocuments() ranks every matching document.',
    timeComplexity: 'O(|terms| × df) per query',
    spaceComplexity: 'O(|terms| × |matched docs|)',
    component: <TfIdfVisualizer />,
    examples: ['binary search', 'hash table', 'machine learning', 'sorting algorithms'],
  },
  {
    id: 'topk',
    icon: <Layers size={22} />,
    name: 'Priority Queue / Top-K',
    tagline: 'Min-Heap for O(N log K) result ranking',
    purpose: 'Efficiently extracts the top-K highest scoring results from N candidates without sorting all N. Uses a min-heap of fixed size K.',
    usage: 'SearchResultRanker.rankTopK(scores, k) runs after TF-IDF scoring to return only the K best results.',
    timeComplexity: 'O(N log K) — better than O(N log N) sort when K ≪ N',
    spaceComplexity: 'O(K) — only K items in heap at any time',
    component: <TopKVisualizer />,
    examples: ['binary search', 'sorting algorithms', 'graph traversal'],
  },
  {
    id: 'levenshtein',
    icon: <Spline size={22} />,
    name: 'Levenshtein Edit Distance',
    tagline: 'Dynamic programming spell correction',
    purpose: 'Computes the minimum number of single-character edits (insert, delete, substitute) to transform one word into another. Powers spell correction.',
    usage: 'SpellCorrector uses LevenshteinDistance.calculate() to find dictionary words within edit distance ≤ 2 of each query term.',
    timeComplexity: 'O(m × n) — m, n = word lengths',
    spaceComplexity: 'O(m × n) — full DP matrix',
    component: <LevenshteinVisualizer />,
    examples: [['algoritm', 'algorithm'], ['kitten', 'sitting'], ['serach', 'search']],
  },
  {
    id: 'pipeline',
    icon: <AlignLeft size={22} />,
    name: 'Text Processing Pipeline',
    tagline: 'Tokenize → Filter → Stem',
    purpose: 'Transforms raw text into canonical search terms through tokenization, stop-word filtering, and Porter stemming.',
    usage: 'TextProcessor.process() runs on every document at index time and on every query at search time.',
    timeComplexity: 'O(n) — n = number of characters in text',
    spaceComplexity: 'O(n) — token list',
    component: <TextPipelineVisualizer />,
    examples: ['The algorithms are searching efficiently', 'Binary trees store sorted data'],
  },
];

export default function AlgorithmsPage() {
  const [activeId, setActiveId] = useState(null);

  const active = ALGORITHMS.find(a => a.id === activeId);

  return (
    <div className="algo-page">
      <div className="algo-hero">
        <h1 className="algo-hero-title">DSA Algorithm Visualizer</h1>
        <p className="algo-hero-subtitle">
          Explore the real data structures and algorithms powering this search engine.
          Every visualization uses live data from the running backend.
        </p>
      </div>

      {/* Algorithm cards grid */}
      <div className="algo-cards-grid">
        {ALGORITHMS.map(algo => (
          <button
            key={algo.id}
            className={`algo-card ${activeId === algo.id ? 'algo-card-active' : ''}`}
            onClick={() => setActiveId(activeId === algo.id ? null : algo.id)}
            aria-expanded={activeId === algo.id}
          >
            <div className="algo-card-icon">{algo.icon}</div>
            <div className="algo-card-body">
              <div className="algo-card-name">{algo.name}</div>
              <div className="algo-card-tagline">{algo.tagline}</div>
            </div>
            <div className={`algo-card-arrow ${activeId === algo.id ? 'open' : ''}`}>▼</div>
          </button>
        ))}
      </div>

      {/* Expanded section */}
      {active && (
        <div className="algo-detail" key={active.id}>
          <div className="algo-detail-header">
            <div className="algo-detail-icon">{active.icon}</div>
            <div>
              <h2 className="algo-detail-title">{active.name}</h2>
              <p className="algo-detail-tagline">{active.tagline}</p>
            </div>
          </div>

          <div className="algo-meta-grid">
            <div className="algo-meta-card">
              <div className="algo-meta-label">Purpose</div>
              <div className="algo-meta-value">{active.purpose}</div>
            </div>
            <div className="algo-meta-card">
              <div className="algo-meta-label">Where Used</div>
              <div className="algo-meta-value">{active.usage}</div>
            </div>
            <div className="algo-meta-card complexity-card">
              <div className="algo-meta-label">Time Complexity</div>
              <code className="complexity-badge time">{active.timeComplexity}</code>
            </div>
            <div className="algo-meta-card complexity-card">
              <div className="algo-meta-label">Space Complexity</div>
              <code className="complexity-badge space">{active.spaceComplexity}</code>
            </div>
          </div>

          {Array.isArray(active.examples[0]) ? (
            <div className="algo-examples">
              <span className="algo-examples-label">Try:</span>
              {active.examples.map((ex, i) => (
                <span key={i} className="algo-example-pair">
                  <code>{ex[0]}</code> → <code>{ex[1]}</code>
                </span>
              ))}
            </div>
          ) : (
            <div className="algo-examples">
              <span className="algo-examples-label">Try:</span>
              {active.examples.map((ex, i) => <code key={i} className="algo-example">{ex}</code>)}
            </div>
          )}

          <div className="algo-viz-panel">
            <div className="algo-viz-title">Interactive Visualization</div>
            {active.component}
          </div>
        </div>
      )}
    </div>
  );
}
