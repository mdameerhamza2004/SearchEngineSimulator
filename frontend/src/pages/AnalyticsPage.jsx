import React, { useState, useEffect } from 'react';
import { analyticsClient } from '../api/analyticsClient';
import { 
  Chart as ChartJS, 
  CategoryScale, 
  LinearScale, 
  BarElement, 
  Title, 
  Tooltip, 
  Legend 
} from 'chart.js';
import { Bar } from 'react-chartjs-2';
import SearchInspector from '../components/analytics/SearchInspector';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
);

const AnalyticsPage = () => {
  const [overview, setOverview] = useState(null);
  const [topQueries, setTopQueries] = useState([]);
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [overviewData, topQueriesData, historyData] = await Promise.all([
        analyticsClient.getOverview(),
        analyticsClient.getTopQueries(),
        analyticsClient.getHistory()
      ]);
      setOverview(overviewData);
      setTopQueries(topQueriesData);
      setHistory(historyData);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleClearHistory = async () => {
    try {
      await analyticsClient.clearHistory();
      fetchData();
    } catch (err) {
      console.error("Failed to clear history:", err);
    }
  };

  if (loading) return <div className="viz-loading">Loading Analytics...</div>;
  if (error) return <div className="viz-error">Error loading analytics: {error}</div>;

  const chartData = {
    labels: topQueries.map(q => q.query),
    datasets: [
      {
        label: 'Search Count',
        data: topQueries.map(q => q.searchCount),
        backgroundColor: 'rgba(59, 130, 246, 0.6)',
        borderColor: 'rgba(59, 130, 246, 1)',
        borderWidth: 1,
      },
    ],
  };
  
  const latencyChartData = {
    labels: topQueries.map(q => q.query),
    datasets: [
      {
        label: 'Avg Latency (ms)',
        data: topQueries.map(q => q.avgLatency),
        backgroundColor: 'rgba(139, 92, 246, 0.6)',
        borderColor: 'rgba(139, 92, 246, 1)',
        borderWidth: 1,
      },
    ],
  };

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: 'top' },
    },
  };

  return (
    <div className="algo-page">
      <div className="algo-hero">
        <h1 className="algo-hero-title">Analytics Dashboard</h1>
        <p className="algo-hero-subtitle">
          Real-time insights into search performance and query intelligence.
        </p>
      </div>

      <div className="algo-cards-grid">
        <div className="algo-card algo-card-active">
          <div className="algo-card-body">
            <h3 className="algo-meta-label">Total Indexed Docs</h3>
            <div className="algo-hero-title" style={{fontSize: '2rem', marginBottom: 0}}>{overview?.totalDocuments}</div>
          </div>
        </div>
        <div className="algo-card algo-card-active">
          <div className="algo-card-body">
            <h3 className="algo-meta-label">Searchable Terms</h3>
            <div className="algo-hero-title" style={{fontSize: '2rem', marginBottom: 0}}>{overview?.totalSearchableTerms}</div>
          </div>
        </div>
        <div className="algo-card algo-card-active">
          <div className="algo-card-body">
            <h3 className="algo-meta-label">Total Searches</h3>
            <div className="algo-hero-title" style={{fontSize: '2rem', marginBottom: 0}}>{overview?.totalSearches}</div>
          </div>
        </div>
        <div className="algo-card algo-card-active">
          <div className="algo-card-body">
            <h3 className="algo-meta-label">Avg Search Latency</h3>
            <div className="algo-hero-title" style={{fontSize: '2rem', marginBottom: 0}}>{overview?.averageLatency?.toFixed(2)} ms</div>
          </div>
        </div>
      </div>
      
      <div className="algo-meta-grid" style={{gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))'}}>
         <div className="algo-meta-card">
          <div className="algo-meta-label">Avg Results per Query</div>
          <div className="algo-meta-value">{overview?.averageResults?.toFixed(1)}</div>
        </div>
        <div className="algo-meta-card">
          <div className="algo-meta-label">Most Common Query</div>
          <div className="algo-meta-value font-mono">"{overview?.mostCommonQuery}"</div>
        </div>
        <div className="algo-meta-card">
          <div className="algo-meta-label">Zero-Result Searches</div>
          <div className="algo-meta-value text-red-500 font-bold">{overview?.zeroResultSearches}</div>
        </div>
      </div>

      {topQueries.length > 0 ? (
        <div className="algo-viz-panel">
          <h2 className="algo-viz-title">Search Performance</h2>
          <div className="algo-meta-grid">
            <div className="algo-card" style={{height: '300px', display: 'block'}}>
              <Bar options={chartOptions} data={chartData} />
            </div>
            <div className="algo-card" style={{height: '300px', display: 'block'}}>
               <Bar options={chartOptions} data={latencyChartData} />
            </div>
          </div>
          
          <h2 className="algo-viz-title" style={{marginTop: '2rem'}}>Top Queries</h2>
          <div className="tfidf-table-wrapper">
            <table className="tfidf-table">
              <thead>
                <tr>
                  <th>Query</th>
                  <th>Search Count</th>
                  <th>Avg Results</th>
                  <th>Avg Latency (ms)</th>
                </tr>
              </thead>
              <tbody>
                {topQueries.map((q, i) => (
                  <tr key={i} className="tfidf-row">
                    <td className="tfidf-title font-mono">{q.query}</td>
                    <td>{q.searchCount}</td>
                    <td>{q.avgResults.toFixed(1)}</td>
                    <td className="tfidf-score-cell">{q.avgLatency.toFixed(2)} ms</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      ) : (
        <div className="viz-placeholder">No historical data available. Perform some searches to see analytics.</div>
      )}
      
      <div className="algo-viz-panel">
         <SearchInspector />
      </div>

      <div className="algo-viz-panel">
        <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem'}}>
          <h2 className="algo-viz-title" style={{margin: 0}}>Search History</h2>
          <button className="viz-btn" onClick={handleClearHistory} disabled={history.length === 0}>
            Clear History
          </button>
        </div>
        
        {history.length > 0 ? (
          <div className="tfidf-table-wrapper">
            <table className="tfidf-table">
              <thead>
                <tr>
                  <th>Time</th>
                  <th>Query</th>
                  <th>Results</th>
                  <th>Latency</th>
                </tr>
              </thead>
              <tbody>
                {history.map((h) => (
                  <tr key={h.id} className="tfidf-row">
                    <td className="viz-muted">{new Date(h.timestamp).toLocaleString()}</td>
                    <td className="tfidf-title font-mono">{h.query}</td>
                    <td>
                      {h.zeroResults ? (
                        <span className="viz-tag" style={{backgroundColor: 'rgba(239, 68, 68, 0.1)', color: '#ef4444'}}>0</span>
                      ) : (
                        h.resultCount
                      )}
                    </td>
                    <td className="tfidf-score-cell">{h.executionTimeMs} ms</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="viz-placeholder">No recent search history.</div>
        )}
      </div>

    </div>
  );
};

export default AnalyticsPage;
