import React, { useState } from 'react';
import SearchBar from './SearchBar';
import SearchResultItem from './SearchResultItem';
import SpellCorrection from './SpellCorrection';
import { apiClient } from '../api/apiClient';

export default function SearchPage() {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searchMeta, setSearchMeta] = useState(null);
  const [spellSuggestion, setSpellSuggestion] = useState(null);
  const [hasSearched, setHasSearched] = useState(false);

  const performSearch = async (searchQuery) => {
    if (!searchQuery || !searchQuery.trim()) return;
    
    setLoading(true);
    setError(null);
    setQuery(searchQuery);
    setHasSearched(true);
    
    try {
      const searchRes = await apiClient.search(searchQuery, 10);
      
      if (searchRes) {
        setResults(searchRes.results || []);
        setSearchMeta({
          totalResults: searchRes.totalResults || 0,
          executionTimeMs: searchRes.executionTimeMs || 0
        });
        
        // The backend SearchResponse field is "correctedQuery"
        if (searchRes.correctedQuery && searchRes.correctedQuery !== searchQuery) {
          setSpellSuggestion(searchRes.correctedQuery);
        } else {
          setSpellSuggestion(null);
        }
      }
    } catch (err) {
      console.error('Search failed:', err);
      setError('Failed to fetch search results. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleCorrectionClick = (correction) => {
    performSearch(correction);
  };

  return (
    <div className="search-page">
      <div className="search-header-area" style={{ flexDirection: 'column', alignItems: 'center', marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '3rem', marginBottom: '1rem', color: 'var(--color-primary)', textAlign: 'center' }}>
          Search Engine Simulator
        </h1>
        <p style={{ fontSize: '1.2rem', color: 'var(--color-text-muted)', maxWidth: '600px', margin: '0 auto 2rem', textAlign: 'center' }}>
          Explore the algorithms that power modern search engines.
        </p>
        <SearchBar query={query} onSearch={performSearch} />
      </div>

      <div className="search-results-container">
        {loading && <div className="loading-state">Searching...</div>}
        
        {error && <div className="error-state">{error}</div>}

        {!loading && !error && spellSuggestion && (
          <SpellCorrection 
            suggestedQuery={spellSuggestion} 
            onCorrectionClick={handleCorrectionClick} 
          />
        )}

        {!loading && !error && searchMeta && (
          <div className="search-meta">
            {searchMeta.totalResults} result{searchMeta.totalResults !== 1 ? 's' : ''} ({searchMeta.executionTimeMs} ms)
          </div>
        )}

        {!loading && !error && results.length > 0 && (
          <div className="results-list">
            {results.map((result, index) => (
              <SearchResultItem key={result.docId || index} item={result} rank={index + 1} />
            ))}
          </div>
        )}
        
        {!loading && !error && hasSearched && results.length === 0 && (
          <div className="empty-state">
            No results found for <strong>{query}</strong>. Try different keywords.
          </div>
        )}
      </div>
    </div>
  );
}
