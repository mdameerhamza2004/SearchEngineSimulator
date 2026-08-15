import React, { useState } from 'react';
import { apiClient } from '../../api/apiClient';

const TermExplorer = () => {
  const [term, setTerm] = useState('');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!term.trim()) return;
    
    setLoading(true);
    setError(null);
    try {
      const data = await apiClient.getInvertedIndexVisualization(term);
      setResult(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2 className="algo-viz-title">Term Explorer</h2>
      <p className="viz-muted" style={{marginBottom: '1.5rem'}}>
        Enter a term to see its exact occurrences across the document corpus using the Inverted Index.
      </p>

      <form onSubmit={handleSearch} className="viz-controls-row" style={{marginBottom: '2rem'}}>
        <input 
          type="text" 
          className="viz-input" 
          placeholder="e.g. algorithm, search, data" 
          value={term}
          onChange={(e) => setTerm(e.target.value)}
        />
        <button type="submit" className="viz-btn primary" disabled={loading || !term.trim()}>
          {loading ? 'Searching...' : 'Explore Term'}
        </button>
      </form>

      {error && <div className="viz-error">{error}</div>}

      {result && (
        <div className="algo-detail" style={{padding: '1.5rem', animation: 'slideInRight 0.3s ease-out forwards'}}>
          <div style={{display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '1.5rem'}}>
             <div className="ii-term-char" style={{fontSize: '2rem', color: 'var(--color-primary)'}}>"{result.term}"</div>
             {result.postings.length === 0 ? (
               <div className="viz-tag" style={{backgroundColor: 'rgba(239, 68, 68, 0.1)', color: '#ef4444'}}>Not found in index</div>
             ) : (
               <div className="viz-tag">Found in {result.postings.length} documents</div>
             )}
          </div>
          
          {result.postings.length > 0 && (
            <div className="ii-postings-list" style={{maxWidth: '100%'}}>
              {result.postings.map((p, idx) => (
                <div key={idx} className="ii-posting-card" style={{animationDelay: `${idx * 0.1}s`}}>
                  <div className="ii-posting-header">
                    <span className="ii-doc-id">Document #{p.docId}</span>
                  </div>
                  <div className="ii-posting-stats" style={{gap: '1rem'}}>
                    <div className="ii-stat-cell">
                      <span className="ii-stat-label">Term Freq</span>
                      <span className="ii-stat-value text-blue-500">{p.termFrequency}</span>
                    </div>
                  </div>
                  <div className="ii-positions">
                    <span className="ii-stat-label" style={{marginRight: '0.5rem'}}>Positions:</span>
                    {p.positions.slice(0, 20).map((pos, i) => (
                      <span key={i} className="ii-pos-badge">{pos}</span>
                    ))}
                    {p.positions.length > 20 && <span className="viz-muted">...and {p.positions.length - 20} more</span>}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default TermExplorer;
