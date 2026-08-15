import React, { useState, useEffect } from 'react';
import { apiClient } from '../../api/apiClient';

const DocumentDetails = ({ docId, onClose }) => {
  const [doc, setDoc] = useState(null);
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        const [docData, statsData] = await Promise.all([
          apiClient.getDocument(docId),
          apiClient.getDocumentStats(docId)
        ]);
        setDoc(docData);
        setStats(statsData);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [docId]);

  return (
    <div style={{
      position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
      backgroundColor: 'rgba(0,0,0,0.5)', zIndex: 1000,
      display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '2rem'
    }}>
      <div className="algo-detail" style={{
        width: '100%', maxWidth: '900px', maxHeight: '90vh', overflowY: 'auto',
        backgroundColor: 'var(--color-surface)', position: 'relative'
      }}>
        <button 
          className="viz-btn" 
          onClick={onClose}
          style={{position: 'absolute', top: '1rem', right: '1rem'}}
        >
          Close
        </button>

        {loading ? (
          <div className="viz-loading">Loading Document Details...</div>
        ) : error ? (
          <div className="viz-error">{error}</div>
        ) : doc && stats && (
          <>
            <h2 className="algo-viz-title" style={{marginBottom: '0.5rem'}}>{doc.title}</h2>
            <div className="viz-tag" style={{display: 'inline-block', marginBottom: '1.5rem'}}>{doc.category}</div>
            
            <div className="algo-meta-grid" style={{gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))'}}>
              <div className="algo-meta-card">
                <div className="algo-meta-label">Total Tokens (Processed)</div>
                <div className="algo-meta-value font-mono font-bold">{stats.tokenCount}</div>
              </div>
              <div className="algo-meta-card">
                <div className="algo-meta-label">Unique Terms</div>
                <div className="algo-meta-value font-mono font-bold">{stats.uniqueTermCount}</div>
              </div>
              <div className="algo-meta-card">
                <div className="algo-meta-label">Document ID</div>
                <div className="algo-meta-value font-mono">#{doc.id}</div>
              </div>
            </div>

            <div className="algo-viz-panel">
              <h3 className="algo-viz-title">DSA Information</h3>
              <p className="viz-muted" style={{marginBottom: '1rem'}}>
                This document is indexed in the <strong>InvertedIndex</strong> via <code>addDocument({doc.id}, tokens)</code>. 
                Its terms are also present in the <strong>Trie</strong> for autocomplete.
                At search time, its final score is computed by the <strong>TfIdfScorer</strong>.
              </p>
              
              <h4 className="pipeline-label" style={{marginBottom: '0.5rem'}}>Most Frequent Terms (TF)</h4>
              <div style={{display: 'flex', flexWrap: 'wrap', gap: '0.5rem'}}>
                {stats.mostFrequentTerms.map((termEntry, i) => (
                  <div key={i} className="algo-example" style={{display: 'flex', alignItems: 'center', gap: '0.5rem'}}>
                    <span className="font-bold">{termEntry.key}</span>
                    <span className="viz-tag" style={{padding: '0.1rem 0.4rem'}}>{termEntry.value}</span>
                  </div>
                ))}
              </div>
            </div>

            <div className="algo-viz-panel">
              <h3 className="algo-viz-title">Document Content</h3>
              <div style={{
                backgroundColor: 'var(--color-background)', 
                padding: '1.5rem', 
                borderRadius: 'var(--radius-md)',
                border: '1px solid var(--color-border)',
                whiteSpace: 'pre-wrap',
                lineHeight: '1.6'
              }}>
                {doc.content}
              </div>
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default DocumentDetails;
