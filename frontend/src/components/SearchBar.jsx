import React, { useState, useEffect, useRef } from 'react';
import { Search, X } from 'lucide-react';
import { apiClient } from '../api/apiClient';

export default function SearchBar({ query: externalQuery, onSearch }) {
  const [query, setQuery] = useState(externalQuery || '');
  const [suggestions, setSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [selectedIndex, setSelectedIndex] = useState(-1);
  const wrapperRef = useRef(null);
  const inputRef = useRef(null);

  // Sync with external query changes (e.g. spell correction click)
  useEffect(() => {
    if (externalQuery !== undefined && externalQuery !== query) {
      setQuery(externalQuery);
    }
  }, [externalQuery]);
  
  // Debounced autocomplete
  useEffect(() => {
    if (query.trim().length === 0) {
      setSuggestions([]);
      return;
    }

    const timer = setTimeout(async () => {
      try {
        const response = await apiClient.autocomplete(query, 7);
        if (response && response.suggestions) {
          setSuggestions(response.suggestions);
        } else {
          setSuggestions([]);
        }
      } catch (error) {
        console.error('Autocomplete error:', error);
        setSuggestions([]);
      }
    }, 300);

    return () => clearTimeout(timer);
  }, [query]);

  // Click outside to close dropdown
  useEffect(() => {
    function handleClickOutside(event) {
      if (wrapperRef.current && !wrapperRef.current.contains(event.target)) {
        setShowSuggestions(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleInputChange = (e) => {
    setQuery(e.target.value);
    setShowSuggestions(true);
    setSelectedIndex(-1);
  };

  const triggerSearch = (searchQuery) => {
    setShowSuggestions(false);
    setSuggestions([]);
    setSelectedIndex(-1);
    onSearch(searchQuery);
  };

  const handleKeyDown = (e) => {
    if (e.key === 'ArrowDown') {
      e.preventDefault();
      if (suggestions.length > 0) {
        setSelectedIndex((prev) => 
          prev < suggestions.length - 1 ? prev + 1 : prev
        );
      }
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      if (suggestions.length > 0) {
        setSelectedIndex((prev) => (prev > 0 ? prev - 1 : -1));
      }
    } else if (e.key === 'Enter') {
      e.preventDefault();
      if (selectedIndex >= 0 && suggestions[selectedIndex]) {
        const selected = suggestions[selectedIndex];
        setQuery(selected);
        triggerSearch(selected);
      } else {
        triggerSearch(query);
      }
    } else if (e.key === 'Escape') {
      setShowSuggestions(false);
      setSelectedIndex(-1);
    }
  };

  const handleClear = () => {
    setQuery('');
    setSuggestions([]);
    setShowSuggestions(false);
    setSelectedIndex(-1);
    inputRef.current?.focus();
  };

  const handleSuggestionClick = (suggestion) => {
    setQuery(suggestion);
    triggerSearch(suggestion);
  };

  return (
    <div className="search-bar-wrapper" ref={wrapperRef} role="combobox" aria-expanded={showSuggestions && suggestions.length > 0} aria-haspopup="listbox">
      <div className="search-bar-container">
        <Search className="search-icon" size={20} aria-hidden="true" />
        <input
          ref={inputRef}
          type="text"
          className="search-input"
          placeholder="Search documents..."
          value={query}
          onChange={handleInputChange}
          onKeyDown={handleKeyDown}
          onFocus={() => query.trim() && setShowSuggestions(true)}
          aria-label="Search query"
          aria-autocomplete="list"
          autoComplete="off"
        />
        {query && (
          <button className="clear-btn" onClick={handleClear} aria-label="Clear search">
            <X size={18} />
          </button>
        )}
      </div>
      
      {showSuggestions && suggestions.length > 0 && (
        <ul className="suggestions-dropdown" role="listbox" aria-label="Autocomplete suggestions">
          {suggestions.map((suggestion, index) => (
            <li 
              key={suggestion}
              className={`suggestion-item ${index === selectedIndex ? 'selected' : ''}`}
              onClick={() => handleSuggestionClick(suggestion)}
              role="option"
              aria-selected={index === selectedIndex}
            >
              <Search size={16} className="suggestion-icon" aria-hidden="true" />
              <span>{suggestion}</span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
