import React from 'react';

export default function SpellCorrection({ suggestedQuery, onCorrectionClick }) {
  if (!suggestedQuery) return null;

  return (
    <div className="spell-correction">
      <span className="spell-text">Did you mean:</span>
      <button 
        className="spell-suggestion-btn"
        onClick={() => onCorrectionClick(suggestedQuery)}
      >
        {suggestedQuery}
      </button>
      <span className="spell-text">?</span>
    </div>
  );
}
