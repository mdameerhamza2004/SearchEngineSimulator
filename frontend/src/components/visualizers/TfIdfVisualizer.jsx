import React, { useState, useCallback } from 'react';
import { Search, RotateCcw } from 'lucide-react';
import { apiClient } from '../../api/apiClient';

/* -----------------------------------------------------------------------
   TF-IDF VISUALIZER
   Shows real ranking pipeline data from /api/visualization/tfidf
----------------------------------------------------------------------- */

const PIPELINE_STEPS = [
  { label: 'Raw Query', key: 'query' },
  { label: 'Tokenization', key: 'rawTokens' },
  { label: 'Stop-word Removal + Stemming', key: 'processedTerms' },
  { label: 'Term Statistics (IDF)', key: 'termStats' },
  { label: 'Document Scores (TF-IDF)', key: 'documentScores' },
];

export default function TfIdfVisualizer() {
  const [inputValue, setInputValue] = useState('');
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [expandedDoc, setExpandedDoc] = useState(null);

  const fetchData = useCallback(async (q) => {
    if (!q.trim()) return;
    setLoading(true);
    setError(null);
    setExpandedDoc(null);
    try {
      const res = await apiClient.getTfIdfVisualization(q);
      setData(res);
    } catch (e) {
      setError('Failed to load TF-IDF data. Is the backend running?');
    } finally {
      setLoading(false);
    }
  }, []);

  const handleSearch = () => fetchData(inputValue.trim());
  const handleReset = () => { setInputValue(''); setData(null); setError(null); };

  return (
    <div className="viz-container">
      <div className="viz-controls-row">
        <input
          className="viz-input"
          placeholder="Enter a query (e.g. binary search, hash table)"
          value={inputValue}
          onChange={e => setInputValue(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && handleSearch()}
        />
        <button className="viz-btn primary" onClick={handleSearch}>
          <Search size={16} /> Compute
        </button>
        <button className="viz-btn" onClick={handleReset}>
          <RotateCcw size={16} /> Reset
        </button>
      </div>

      {loading && <div className="viz-loading">Computing TF-IDF scores…</div>}
      {error && <div className="viz-error">{error}</div>}

      {data && (
        <>
          {/* Pipeline flow */}
          <div className="tfidf-pipeline">
            <div className="pipeline-step">
              <div className="pipeline-label">Query</div>
              <div className="pipeline-value"><code>"{data.query}"</code></div>
              <div className="pipeline-arrow">↓</div>
            </div>
            <div className="pipeline-step">
              <div className="pipeline-label">Tokenization</div>
              <div className="pipeline-value">{data.rawTokens.map((t, i) => <span key={i} className="viz-tag">{t}</span>)}</div>
              <div className="pipeline-arrow">↓</div>
            </div>
            <div className="pipeline-step">
              <div className="pipeline-label">After Stop-word Removal + Stemming</div>
              <div className="pipeline-value">{data.processedTerms.map((t, i) => <span key={i} className="viz-tag active">{t}</span>)}</div>
              <div className="pipeline-arrow">↓</div>
            </div>
            <div className="pipeline-step">
              <div className="pipeline-label">IDF per Term (log N/df)</div>
              <div className="pipeline-value tfidf-term-stats">
                {data.termStats.map(ts => (
                  <div key={ts.term} className="tfidf-term-stat-card">
                    <span className="tfidf-term">{ts.term}</span>
                    <span className="tfidf-meta">df={ts.documentFrequency}</span>
                    <span className="tfidf-idf">IDF={ts.idf.toFixed(4)}</span>
                  </div>
                ))}
              </div>
              <div className="pipeline-arrow">↓</div>
            </div>
            <div className="pipeline-step">
              <div className="pipeline-label">TF × IDF → Ranked Documents</div>
            </div>
          </div>

          {/* Document scores table */}
          {data.documentScores.length === 0 ? (
            <div className="viz-empty">No documents matched this query.</div>
          ) : (
            <>
              <div className="tfidf-table-title">
                Score Breakdown — {data.documentScores.length} matching documents
              </div>
              <div className="tfidf-table-wrapper">
                <table className="tfidf-table">
                  <thead>
                    <tr>
                      <th>#</th>
                      <th>Document</th>
                      <th>Category</th>
                      {data.termStats.map(ts => (
                        <th key={ts.term}><code>{ts.term}</code><br /><small>TF-IDF</small></th>
                      ))}
                      <th>Final Score</th>
                    </tr>
                  </thead>
                  <tbody>
                    {data.documentScores.map((doc, rank) => {
                      const termMap = {};
                      doc.termBreakdown.forEach(tb => { termMap[tb.term] = tb; });
                      return (
                        <React.Fragment key={doc.docId}>
                          <tr
                            className={`tfidf-row ${expandedDoc === doc.docId ? 'expanded' : ''}`}
                            onClick={() => setExpandedDoc(expandedDoc === doc.docId ? null : doc.docId)}
                            style={{ cursor: 'pointer' }}
                          >
                            <td className="tfidf-rank">#{rank + 1}</td>
                            <td className="tfidf-title">{doc.title}</td>
                            <td><span className="viz-tag">{doc.category}</span></td>
                            {data.termStats.map(ts => {
                              const tb = termMap[ts.term];
                              return (
                                <td key={ts.term} className="tfidf-score-cell">
                                  {tb ? tb.tfIdf.toFixed(4) : <span className="viz-muted">—</span>}
                                </td>
                              );
                            })}
                            <td className="tfidf-final">{doc.finalScore.toFixed(4)}</td>
                          </tr>
                          {expandedDoc === doc.docId && (
                            <tr className="tfidf-detail-row">
                              <td colSpan={4 + data.termStats.length}>
                                <div className="tfidf-detail">
                                  <strong>Detailed Breakdown for "{doc.title}"</strong>
                                  <table className="tfidf-detail-table">
                                    <thead>
                                      <tr><th>Term</th><th>TF (freq/len)</th><th>DF</th><th>IDF</th><th>TF-IDF</th></tr>
                                    </thead>
                                    <tbody>
                                      {doc.termBreakdown.map(tb => (
                                        <tr key={tb.term}>
                                          <td><code>{tb.term}</code></td>
                                          <td>{tb.termFrequency}/{tb.documentLength} = {tb.tf.toFixed(5)}</td>
                                          <td>{data.termStats.find(t => t.term === tb.term)?.documentFrequency ?? '?'}</td>
                                          <td>{tb.idf.toFixed(5)}</td>
                                          <td><strong>{tb.tfIdf.toFixed(5)}</strong></td>
                                        </tr>
                                      ))}
                                    </tbody>
                                  </table>
                                </div>
                              </td>
                            </tr>
                          )}
                        </React.Fragment>
                      );
                    })}
                  </tbody>
                </table>
              </div>
              <p className="viz-hint">Click any row to see the full per-term calculation.</p>
            </>
          )}
        </>
      )}

      {!data && !loading && (
        <div className="viz-placeholder">
          Enter a query to see the complete TF-IDF ranking pipeline.
          Try: <code>binary search</code>, <code>hash table</code>, <code>machine learning</code>
        </div>
      )}
    </div>
  );
}
