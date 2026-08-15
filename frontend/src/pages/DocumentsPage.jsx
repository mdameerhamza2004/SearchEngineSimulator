import React, { useState, useEffect } from 'react';
import { apiClient } from '../api/apiClient';
import DocumentDetails from '../components/documents/DocumentDetails';
import TermExplorer from '../components/documents/TermExplorer';

const DocumentsPage = () => {
  const [documents, setDocuments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedDocId, setSelectedDocId] = useState(null);
  const [sortConfig, setSortConfig] = useState({ key: 'id', direction: 'asc' });

  // Pagination
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  useEffect(() => {
    fetchDocuments();
  }, []);

  const fetchDocuments = async () => {
    setLoading(true);
    try {
      const data = await apiClient.getDocuments();
      setDocuments(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleSort = (key) => {
    let direction = 'asc';
    if (sortConfig.key === key && sortConfig.direction === 'asc') {
      direction = 'desc';
    }
    setSortConfig({ key, direction });
  };

  const sortedDocuments = [...documents].sort((a, b) => {
    let aVal = a[sortConfig.key];
    let bVal = b[sortConfig.key];
    
    // Sort logic
    if (typeof aVal === 'string') aVal = aVal.toLowerCase();
    if (typeof bVal === 'string') bVal = bVal.toLowerCase();

    if (aVal < bVal) {
      return sortConfig.direction === 'asc' ? -1 : 1;
    }
    if (aVal > bVal) {
      return sortConfig.direction === 'asc' ? 1 : -1;
    }
    return 0;
  });

  const filteredDocuments = sortedDocuments.filter(doc => 
    doc.title.toLowerCase().includes(searchTerm.toLowerCase()) || 
    (doc.category && doc.category.toLowerCase().includes(searchTerm.toLowerCase()))
  );

  const totalPages = Math.ceil(filteredDocuments.length / itemsPerPage);
  const currentDocuments = filteredDocuments.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  if (loading && documents.length === 0) return <div className="viz-loading">Loading Documents...</div>;
  if (error) return <div className="viz-error">Error loading documents: {error}</div>;

  return (
    <div className="algo-page">
      <div className="algo-hero" style={{marginBottom: '2rem'}}>
        <h1 className="algo-hero-title">Document Intelligence</h1>
        <p className="algo-hero-subtitle">
          Explore the indexed corpus, document statistics, and term distributions.
        </p>
      </div>

      <div className="algo-viz-panel" style={{borderTop: 'none', paddingTop: 0}}>
         <TermExplorer />
      </div>

      <div className="algo-viz-panel">
        <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '1rem'}}>
          <h2 className="algo-viz-title" style={{margin: 0}}>Indexed Documents ({filteredDocuments.length})</h2>
          <input 
            type="text" 
            className="viz-input" 
            placeholder="Filter by title or category..." 
            value={searchTerm}
            onChange={(e) => {
              setSearchTerm(e.target.value);
              setCurrentPage(1);
            }}
          />
        </div>

        <div className="tfidf-table-wrapper">
          <table className="tfidf-table">
            <thead>
              <tr>
                <th style={{cursor: 'pointer'}} onClick={() => handleSort('id')}>
                  ID {sortConfig.key === 'id' && (sortConfig.direction === 'asc' ? '↑' : '↓')}
                </th>
                <th style={{cursor: 'pointer'}} onClick={() => handleSort('title')}>
                  Title {sortConfig.key === 'title' && (sortConfig.direction === 'asc' ? '↑' : '↓')}
                </th>
                <th style={{cursor: 'pointer'}} onClick={() => handleSort('category')}>
                  Category {sortConfig.key === 'category' && (sortConfig.direction === 'asc' ? '↑' : '↓')}
                </th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {currentDocuments.length > 0 ? currentDocuments.map((doc) => (
                <tr key={doc.id} className="tfidf-row">
                  <td className="viz-muted">#{doc.id}</td>
                  <td className="tfidf-title">{doc.title}</td>
                  <td>
                    <span className="viz-tag">{doc.category || 'Uncategorized'}</span>
                  </td>
                  <td>
                    <span className="viz-tag" style={{backgroundColor: 'rgba(16, 185, 129, 0.1)', color: '#10b981'}}>Indexed</span>
                  </td>
                  <td>
                    <button className="viz-btn" onClick={() => setSelectedDocId(doc.id)}>
                      Details
                    </button>
                  </td>
                </tr>
              )) : (
                <tr>
                  <td colSpan="5" className="viz-placeholder">No documents found matching your filter.</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
        
        {totalPages > 1 && (
          <div className="viz-step-controls" style={{marginTop: '1.5rem'}}>
            <button 
              className="viz-btn" 
              disabled={currentPage === 1}
              onClick={() => setCurrentPage(p => p - 1)}
            >
              Previous
            </button>
            <span className="viz-step-label">Page {currentPage} of {totalPages}</span>
            <button 
              className="viz-btn" 
              disabled={currentPage === totalPages}
              onClick={() => setCurrentPage(p => p + 1)}
            >
              Next
            </button>
          </div>
        )}
      </div>

      {selectedDocId && (
        <DocumentDetails 
          docId={selectedDocId} 
          onClose={() => setSelectedDocId(null)} 
        />
      )}
    </div>
  );
};

export default DocumentsPage;
