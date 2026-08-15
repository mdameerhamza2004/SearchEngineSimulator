import React from 'react';

export default function SearchResultItem({ item, rank }) {
  const { docId, title, snippet, score, category, matchedTerms, scoreBreakdown } = item;

  // Highlight matched terms in the snippet
  const createHighlightedSnippet = (text, terms) => {
    if (!text || !terms || terms.length === 0) return text || '';
    
    // Escape terms for regex
    const escapedTerms = terms.map(t => t.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'));
    const regex = new RegExp(`(${escapedTerms.join('|')})`, 'gi');
    
    const parts = text.split(regex);
    
    return parts.map((part, index) => {
      const isMatch = terms.some(term => term.toLowerCase() === part.toLowerCase());
      if (isMatch) {
        return <mark key={index} className="highlight">{part}</mark>;
      }
      return <span key={index}>{part}</span>;
    });
  };

  return (
    <div className="search-result-item">
      <div className="result-header">
        <div className="result-meta">
          {category && <span className="result-category">{category}</span>}
          {rank && <span className="result-rank">#{rank}</span>}
        </div>
        <h3 className="result-title">{title}</h3>
      </div>
      
      <p className="result-snippet">
        {createHighlightedSnippet(snippet, matchedTerms)}
      </p>
      
      <details className="result-details">
        <summary>Why this result? (Score: {score?.toFixed(4) || 0})</summary>
        <div className="details-content">
          <div className="details-section">
            <h4>Matched Terms</h4>
            <div className="tags">
              {matchedTerms && matchedTerms.length > 0 ? (
                matchedTerms.map((term, i) => <span key={i} className="tag">{term}</span>)
              ) : (
                <span className="text-muted">None</span>
              )}
            </div>
          </div>
          
          {scoreBreakdown && Object.keys(scoreBreakdown).length > 0 && (
            <div className="details-section">
              <h4>TF-IDF Score Breakdown</h4>
              <ul className="score-breakdown">
                {Object.entries(scoreBreakdown).map(([term, value]) => (
                  <li key={term}>
                    <span className="score-key">{term}:</span>
                    <span className="score-value">{typeof value === 'number' ? value.toFixed(6) : value}</span>
                  </li>
                ))}
              </ul>
            </div>
          )}

          <div className="details-section">
            <h4>Ranking</h4>
            <p style={{ color: 'var(--color-text-muted)', fontSize: '0.85rem' }}>
              Position #{rank} · Document ID: {docId} · Combined TF-IDF Score: {score?.toFixed(6)}
            </p>
          </div>
        </div>
      </details>
    </div>
  );
}
