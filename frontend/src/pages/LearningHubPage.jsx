import React, { useState, useEffect } from 'react';
import { learningClient } from '../api/learningClient';
import { Link } from 'react-router-dom';

const LearningHubPage = () => {
  const [topics, setTopics] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchTopics = async () => {
      try {
        const data = await learningClient.getTopics();
        setTopics(data);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    fetchTopics();
  }, []);

  if (loading) return <div className="viz-loading">Loading Learning Hub...</div>;
  if (error) return <div className="viz-error">{error}</div>;

  return (
    <div className="algo-page">
      <div className="algo-hero">
        <h1 className="algo-hero-title">DSA Learning Platform</h1>
        <p className="algo-hero-subtitle">
          Master Data Structures and Algorithms with structured lessons, interactive visualizations, and placement-ready practice.
        </p>
      </div>

      <div className="algo-viz-panel">
        <h2 className="algo-viz-title">Core Topics</h2>
        <div className="algo-cards-grid">
          {topics.map(topic => (
            <Link to={`/learn/${topic.slug}`} key={topic.id} style={{textDecoration: 'none'}}>
              <div className="algo-card" style={{cursor: 'pointer'}}>
                <div className="algo-card-body">
                  <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.5rem'}}>
                    <h3 className="algo-card-title" style={{margin: 0}}>{topic.title}</h3>
                    <span className="viz-tag" style={{fontSize: '0.75rem'}}>{topic.difficulty}</span>
                  </div>
                  <p className="algo-card-description">{topic.overview}</p>
                </div>
              </div>
            </Link>
          ))}
        </div>
      </div>
    </div>
  );
};

export default LearningHubPage;
