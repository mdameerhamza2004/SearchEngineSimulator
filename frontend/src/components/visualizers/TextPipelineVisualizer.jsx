import React, { useState, useCallback } from 'react';
import { Search, RotateCcw } from 'lucide-react';
import { apiClient } from '../../api/apiClient';

/* -----------------------------------------------------------------------
   TEXT PROCESSING PIPELINE VISUALIZER
   Shows tokens at each stage from real TextProcessor
----------------------------------------------------------------------- */

const STAGES = [
  { key: 'originalText', label: 'Original Text', desc: 'Raw input before any processing', isString: true },
  { key: 'afterTokenization', label: 'Tokenization', desc: 'Split on whitespace/punctuation, convert to lowercase' },
  { key: 'afterStopWordRemoval', label: 'Stop-word Removal', desc: 'Remove common words (the, is, are, at, …)' },
  { key: 'afterStemming', label: 'Stemming (Porter)', desc: 'Reduce words to root form (running→run, search→search)' },
];

export default function TextPipelineVisualizer() {
  const [inputValue, setInputValue] = useState('');
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchData = useCallback(async (text) => {
    if (!text.trim()) return;
    setLoading(true);
    setError(null);
    try {
      const res = await apiClient.getTextPipelineVisualization(text);
      setData(res);
    } catch (e) {
      setError('Failed to process text. Is the backend running?');
    } finally {
      setLoading(false);
    }
  }, []);

  const handleProcess = () => fetchData(inputValue.trim());
  const handleReset = () => { setInputValue(''); setData(null); setError(null); };

  const getTokenClass = (token, stage, data) => {
    if (stage === 'afterStemming') {
      const change = data.stemChanges.find(sc => sc.stemmed === token);
      if (change && change.changed) return 'pipeline-token stemmed';
    }
    if (stage === 'afterStopWordRemoval') {
      return 'pipeline-token filtered';
    }
    if (stage === 'afterTokenization') {
      const inRemoved = data.removedStopWords.includes(token);
      if (inRemoved) return 'pipeline-token will-remove';
    }
    return 'pipeline-token';
  };

  return (
    <div className="viz-container">
      <div className="viz-controls-row">
        <input
          className="viz-input"
          placeholder="Enter text to process (e.g. The algorithms are searching efficiently)"
          value={inputValue}
          onChange={e => setInputValue(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && handleProcess()}
          style={{ flex: 1, maxWidth: 500 }}
        />
        <button className="viz-btn primary" onClick={handleProcess}>
          <Search size={16} /> Process
        </button>
        <button className="viz-btn" onClick={handleReset}>
          <RotateCcw size={16} /> Reset
        </button>
      </div>

      {loading && <div className="viz-loading">Processing text through pipeline…</div>}
      {error && <div className="viz-error">{error}</div>}

      {data && (
        <>
          <div className="pipeline-viz">
            {STAGES.map((stage, idx) => {
              const tokens = stage.isString ? null : data[stage.key];
              return (
                <div key={stage.key} className="pipeline-stage" style={{ animationDelay: `${idx * 100}ms` }}>
                  <div className="pipeline-stage-header">
                    <div className="pipeline-stage-num">{idx + 1}</div>
                    <div>
                      <div className="pipeline-stage-label">{stage.label}</div>
                      <div className="pipeline-stage-desc">{stage.desc}</div>
                    </div>
                  </div>
                  <div className="pipeline-stage-content">
                    {stage.isString ? (
                      <span className="pipeline-raw-text">"{data.originalText}"</span>
                    ) : tokens.length === 0 ? (
                      <span className="viz-muted">(empty)</span>
                    ) : (
                      tokens.map((token, i) => (
                        <span key={i} className={getTokenClass(token, stage.key, data)}
                          title={stage.key === 'afterStemming'
                            ? data.stemChanges.find(sc => sc.stemmed === token)?.changed
                              ? `Stemmed from: ${data.stemChanges.find(sc => sc.stemmed === token)?.original}`
                              : 'Unchanged'
                            : undefined}
                        >
                          {token}
                        </span>
                      ))
                    )}
                  </div>
                  {idx < STAGES.length - 1 && (
                    <div className="pipeline-arrow-down">↓</div>
                  )}
                </div>
              );
            })}
          </div>

          {/* Summary cards */}
          <div className="pipeline-summary">
            {data.removedStopWords.length > 0 && (
              <div className="pipeline-summary-card removed">
                <div className="pipeline-summary-title">Removed Stop Words ({data.removedStopWords.length})</div>
                <div>{data.removedStopWords.map((w, i) => <span key={i} className="pipeline-token will-remove">{w}</span>)}</div>
              </div>
            )}
            {data.stemChanges.filter(sc => sc.changed).length > 0 && (
              <div className="pipeline-summary-card changed">
                <div className="pipeline-summary-title">Stemming Changes</div>
                <div className="stem-changes">
                  {data.stemChanges.filter(sc => sc.changed).map((sc, i) => (
                    <div key={i} className="stem-change-item">
                      <span className="stem-original">{sc.original}</span>
                      <span className="stem-arrow">→</span>
                      <span className="stem-result">{sc.stemmed}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        </>
      )}

      {!data && !loading && (
        <div className="viz-placeholder">
          Enter text to see how it moves through the processing pipeline.
          Try: <code>The algorithms are searching efficiently through trees</code>
        </div>
      )}
    </div>
  );
}
