import React, { useState, useCallback } from 'react';
import { Search, RotateCcw } from 'lucide-react';
import { apiClient } from '../../api/apiClient';

/* -----------------------------------------------------------------------
   INVERTED INDEX VISUALIZER
   Shows real postings for a term from the actual InvertedIndex
----------------------------------------------------------------------- */

export default function InvertedIndexVisualizer() {
  const [term, setTerm] = useState('');
  const [inputValue, setInputValue] = useState('');
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchData = useCallback(async (t) => {
    if (!t.trim()) return;
    setLoading(true);
    setError(null);
    try {
      const res = await apiClient.getInvertedIndexVisualization(t);
      setData(res);
    } catch (e) {
      setError('Failed to load index data. Is the backend running?');
    } finally {
      setLoading(false);
    }
  }, []);

  const handleSearch = () => {
    const t = inputValue.trim();
    setTerm(t);
    fetchData(t);
  };

  const handleReset = () => {
    setInputValue('');
    setTerm('');
    setData(null);
    setError(null);
  };

  return (
    <div className="viz-container">
      <div className="viz-controls-row">
        <input
          className="viz-input"
          placeholder="Enter a term (e.g. algorithm, tree, hash)"
          value={inputValue}
          onChange={e => setInputValue(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && handleSearch()}
        />
        <button className="viz-btn primary" onClick={handleSearch}>
          <Search size={16} /> Lookup
        </button>
        <button className="viz-btn" onClick={handleReset}>
          <RotateCcw size={16} /> Reset
        </button>
      </div>

      {loading && <div className="viz-loading">Looking up term…</div>}
      {error && <div className="viz-error">{error}</div>}

      {data && (
        <>
          <div className="viz-stats-row">
            <span className="viz-stat">
              Term in index: <code className="viz-code">{data.term}</code>
              {data.term !== data.originalTerm && (
                <span className="viz-muted"> (stemmed from "{data.originalTerm}")</span>
              )}
            </span>
            <span className="viz-stat">
              Document Frequency: <strong>{data.documentFrequency}</strong> / {data.totalDocuments}
            </span>
            <span className="viz-stat">
              IDF ≈ <strong>{data.totalDocuments > 0 && data.documentFrequency > 0
                ? Math.log(data.totalDocuments / data.documentFrequency).toFixed(4)
                : '0'}</strong>
            </span>
          </div>

          {data.postings.length === 0 ? (
            <div className="viz-empty">
              Term <code>{data.term}</code> not found in the inverted index.
              {data.term !== data.originalTerm && ` (searched for stemmed form of "${data.originalTerm}")`}
            </div>
          ) : (
            <div className="ii-diagram">
              {/* Term node */}
              <div className="ii-term-node">
                <div className="ii-term-label">
                  <span className="ii-term-char">{data.term}</span>
                  <span className="ii-term-badge">Term</span>
                </div>
                <div className="ii-connector-down" />
              </div>

              {/* Postings list */}
              <div className="ii-postings-list">
                {data.postings.map((posting, i) => (
                  <div key={posting.docId} className="ii-posting-card" style={{ animationDelay: `${i * 60}ms` }}>
                    <div className="ii-posting-header">
                      <span className="ii-doc-id">Doc #{posting.docId}</span>
                      {posting.category && <span className="viz-tag">{posting.category}</span>}
                    </div>
                    <div className="ii-doc-title">{posting.title}</div>
                    <div className="ii-posting-stats">
                      <div className="ii-stat-cell">
                        <span className="ii-stat-label">Term Freq</span>
                        <span className="ii-stat-value">{posting.termFrequency}</span>
                      </div>
                      <div className="ii-stat-cell">
                        <span className="ii-stat-label">Doc Length</span>
                        <span className="ii-stat-value">{posting.documentLength}</span>
                      </div>
                      <div className="ii-stat-cell">
                        <span className="ii-stat-label">TF</span>
                        <span className="ii-stat-value">{posting.tf.toFixed(5)}</span>
                      </div>
                    </div>
                    {posting.positions && posting.positions.length > 0 && (
                      <div className="ii-positions">
                        Positions: {posting.positions.slice(0, 10).map((p, pi) => (
                          <span key={pi} className="ii-pos-badge">{p}</span>
                        ))}
                        {posting.positions.length > 10 && <span className="viz-muted">+{posting.positions.length - 10} more</span>}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>
          )}
        </>
      )}

      {!data && !loading && (
        <div className="viz-placeholder">
          Enter a term to see its postings list from the real inverted index.
          Try: <code>algorithm</code>, <code>tree</code>, <code>sort</code>
        </div>
      )}
    </div>
  );
}
