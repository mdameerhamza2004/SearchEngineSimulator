import React, { useState } from 'react';
import { analyticsClient } from '../../api/analyticsClient';

const SearchInspector = () => {
  const [query, setQuery] = useState('');
  const [inspection, setInspection] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleInspect = async (e) => {
    e.preventDefault();
    if (!query.trim()) return;
    
    setLoading(true);
    setError(null);
    try {
      const data = await analyticsClient.inspectSearch(query);
      setInspection(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="algo-detail">
      <h2 className="algo-viz-title">Search Performance Inspector</h2>
      <p className="viz-muted" style={{marginBottom: '1.5rem'}}>
        Enter a query to run a live search and measure the exact latency of each internal pipeline stage.
      </p>
      
      <form onSubmit={handleInspect} className="viz-controls-row" style={{marginBottom: '2rem'}}>
        <input 
          type="text" 
          className="viz-input" 
          placeholder="Enter query to inspect..." 
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          style={{flex: 1}}
        />
        <button type="submit" className="viz-btn primary" disabled={loading || !query.trim()}>
          {loading ? 'Inspecting...' : 'Inspect Execution'}
        </button>
      </form>

      {error && <div className="viz-error">{error}</div>}

      {inspection && (
        <div className="pipeline-viz">
           <div className="pipeline-stage-content" style={{flexDirection: 'column', alignItems: 'flex-start'}}>
              <h3 className="pipeline-stage-label" style={{marginBottom: '1rem'}}>
                Query: <span className="pipeline-raw-text">"{inspection.query}"</span>
              </h3>
              
              <div style={{width: '100%'}}>
                 <div style={{display: 'flex', justifyContent: 'space-between', padding: '0.75rem', borderBottom: '1px solid var(--color-border)'}}>
                    <span className="pipeline-label">1. Tokenization & Stemming</span>
                    <span className="viz-code">{inspection.tokenizationMs.toFixed(3)} ms</span>
                 </div>
                 <div style={{display: 'flex', justifyContent: 'space-between', padding: '0.75rem', borderBottom: '1px solid var(--color-border)'}}>
                    <span className="pipeline-label">2. Inverted Index Lookup</span>
                    <span className="viz-code">{inspection.indexLookupMs.toFixed(3)} ms</span>
                 </div>
                 <div style={{display: 'flex', justifyContent: 'space-between', padding: '0.75rem', borderBottom: '1px solid var(--color-border)'}}>
                    <span className="pipeline-label">3. TF-IDF Scoring</span>
                    <span className="viz-code">{inspection.tfIdfScoringMs.toFixed(3)} ms</span>
                 </div>
                 <div style={{display: 'flex', justifyContent: 'space-between', padding: '0.75rem', borderBottom: '1px solid var(--color-border)'}}>
                    <span className="pipeline-label">4. PriorityQueue Top-K Ranking</span>
                    <span className="viz-code">{inspection.rankingMs.toFixed(3)} ms</span>
                 </div>
                 <div style={{display: 'flex', justifyContent: 'space-between', padding: '0.75rem', backgroundColor: 'rgba(59, 130, 246, 0.05)', fontWeight: 'bold'}}>
                    <span className="pipeline-label" style={{color: 'var(--color-primary)'}}>Total Execution Time</span>
                    <span className="tfidf-final">{inspection.totalMs.toFixed(3)} ms</span>
                 </div>
                 <div style={{display: 'flex', justifyContent: 'space-between', padding: '0.75rem'}}>
                    <span className="pipeline-label">Candidate Documents Scored</span>
                    <span>{inspection.totalResults} documents</span>
                 </div>
              </div>
           </div>
        </div>
      )}
    </div>
  );
};

export default SearchInspector;
