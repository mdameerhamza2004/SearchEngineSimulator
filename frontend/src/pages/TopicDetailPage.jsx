import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { learningClient } from '../api/learningClient';
import InteractiveQuiz from '../components/learning/InteractiveQuiz';
import TrieVisualizer from '../components/visualizers/TrieVisualizer';
import InvertedIndexVisualizer from '../components/visualizers/InvertedIndexVisualizer';
import TfIdfVisualizer from '../components/visualizers/TfIdfVisualizer';

const TopicDetailPage = () => {
  const { slug } = useParams();
  const [topic, setTopic] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeTab, setActiveTab] = useState('learn');

  useEffect(() => {
    const fetchTopic = async () => {
      setLoading(true);
      try {
        const data = await learningClient.getTopic(slug);
        setTopic(data);
        setActiveTab('learn');
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };
    fetchTopic();
  }, [slug]);

  if (loading) return <div className="viz-loading">Loading Topic...</div>;
  if (error) return <div className="viz-error">{error}</div>;
  if (!topic) return <div className="viz-placeholder">Topic not found.</div>;

  const renderVisualizer = () => {
    if (slug === 'trie') return <TrieVisualizer />;
    if (slug === 'inverted-index') return <InvertedIndexVisualizer />;
    if (slug === 'tf-idf' || slug === 'tf-idf-ranking') return <TfIdfVisualizer />;
    return <div className="viz-placeholder">Interactive visualization for this topic is coming soon!</div>;
  };

  return (
    <div className="algo-page" style={{display: 'flex', gap: '2rem'}}>
      
      {/* Sidebar Content Index */}
      <div style={{width: '250px', flexShrink: 0}} className="hidden md:block">
        <div className="algo-viz-panel" style={{position: 'sticky', top: '2rem'}}>
          <h3 className="algo-viz-title" style={{fontSize: '1.25rem'}}>Contents</h3>
          <ul style={{listStyle: 'none', padding: 0, display: 'flex', flexDirection: 'column', gap: '0.5rem'}}>
            <li><button className={`viz-btn ${activeTab === 'learn' ? 'primary' : ''}`} style={{width: '100%', textAlign: 'left'}} onClick={() => setActiveTab('learn')}>📚 Learn</button></li>
            <li><button className={`viz-btn ${activeTab === 'internal' ? 'primary' : ''}`} style={{width: '100%', textAlign: 'left'}} onClick={() => setActiveTab('internal')}>⚙️ Internal Working & Vis</button></li>
            <li><button className={`viz-btn ${activeTab === 'code' ? 'primary' : ''}`} style={{width: '100%', textAlign: 'left'}} onClick={() => setActiveTab('code')}>💻 Implementations</button></li>
            <li><button className={`viz-btn ${activeTab === 'practice' ? 'primary' : ''}`} style={{width: '100%', textAlign: 'left'}} onClick={() => setActiveTab('practice')}>✍️ Practice & MCQs</button></li>
            <li><button className={`viz-btn ${activeTab === 'interview' ? 'primary' : ''}`} style={{width: '100%', textAlign: 'left'}} onClick={() => setActiveTab('interview')}>🎙️ Interview Questions</button></li>
          </ul>

          {topic.relatedTopics && topic.relatedTopics.length > 0 && (
            <div style={{marginTop: '2rem'}}>
              <h3 className="algo-meta-label">Related Topics</h3>
              <div style={{display: 'flex', flexDirection: 'column', gap: '0.5rem'}}>
                {topic.relatedTopics.map((rel, idx) => (
                  <Link key={idx} to={`/learn/${rel}`} className="viz-link" style={{fontSize: '0.875rem'}}>{rel}</Link>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Main Content */}
      <div style={{flex: 1, minWidth: 0}}>
        <div className="algo-hero" style={{marginBottom: '2rem'}}>
          <div style={{display: 'flex', justifyContent: 'center', gap: '0.5rem', marginBottom: '1rem'}}>
            <span className="viz-tag">{topic.difficulty}</span>
          </div>
          <h1 className="algo-hero-title">{topic.title}</h1>
          <p className="algo-hero-subtitle">{topic.overview}</p>
        </div>

        {activeTab === 'learn' && (
          <div className="algo-viz-panel animation-fade-in">
            <h2 className="algo-viz-title">Learning Objectives</h2>
            <ul style={{marginBottom: '2rem', paddingLeft: '1.5rem'}}>
              {topic.learningObjectives?.map((obj, i) => <li key={i} style={{marginBottom: '0.5rem'}}>{obj}</li>)}
            </ul>

            {topic.coreConcepts && topic.coreConcepts.length > 0 && (
              <>
                <h2 className="algo-viz-title">Core Concepts</h2>
                <ul style={{marginBottom: '2rem', paddingLeft: '1.5rem'}}>
                  {topic.coreConcepts.map((concept, i) => <li key={i} style={{marginBottom: '0.5rem'}}>{concept}</li>)}
                </ul>
              </>
            )}

            <h2 className="algo-viz-title">In-Depth Explanation</h2>
            <div className="algo-prose" dangerouslySetInnerHTML={{__html: topic.explanation}} style={{marginBottom: '2rem'}}></div>

            <h2 className="algo-viz-title">Complexity Analysis</h2>
            <div className="algo-meta-grid" style={{gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))'}}>
              <div className="algo-meta-card">
                <div className="algo-meta-label">Time Complexity</div>
                <div className="algo-meta-value font-mono">{topic.timeComplexity}</div>
              </div>
              <div className="algo-meta-card">
                <div className="algo-meta-label">Space Complexity</div>
                <div className="algo-meta-value font-mono">{topic.spaceComplexity}</div>
              </div>
            </div>

            <h2 className="algo-viz-title" style={{marginTop: '2rem'}}>Real-World Applications</h2>
            <ul style={{paddingLeft: '1.5rem', marginBottom: '2rem'}}>
              {topic.realWorldApplications?.map((app, i) => <li key={i} style={{marginBottom: '0.5rem'}}>{app}</li>)}
            </ul>

            {topic.edgeCases && topic.edgeCases.length > 0 && (
              <>
                <h2 className="algo-viz-title">Edge Cases to Consider</h2>
                <ul style={{paddingLeft: '1.5rem', marginBottom: '2rem', color: 'var(--color-text-muted)'}}>
                  {topic.edgeCases.map((ec, i) => <li key={i} style={{marginBottom: '0.5rem'}}>{ec}</li>)}
                </ul>
              </>
            )}
          </div>
        )}

        {activeTab === 'internal' && (
          <div className="algo-viz-panel animation-fade-in">
            {topic.visualExplanation && (
              <>
                <h2 className="algo-viz-title">Visual Explanation</h2>
                <div className="algo-prose" dangerouslySetInnerHTML={{__html: topic.visualExplanation}} style={{marginBottom: '2rem', padding: '1.5rem', backgroundColor: 'var(--color-background)', borderRadius: 'var(--radius-md)'}}></div>
              </>
            )}
            
            <h2 className="algo-viz-title">Internal Working</h2>
            <div className="algo-prose" dangerouslySetInnerHTML={{__html: topic.internalWorking}} style={{marginBottom: '2rem'}}></div>
            
            <div style={{marginTop: '2rem', borderTop: '1px solid var(--color-border)', paddingTop: '2rem'}}>
              {renderVisualizer()}
            </div>
          </div>
        )}

        {activeTab === 'code' && (
          <div className="algo-viz-panel animation-fade-in">
            <h2 className="algo-viz-title">Algorithm</h2>
            <div className="algo-prose" dangerouslySetInnerHTML={{__html: topic.algorithm}} style={{marginBottom: '2rem'}}></div>

            {topic.importantPatterns && topic.importantPatterns.length > 0 && (
              <>
                <h2 className="algo-viz-title">Important Patterns & Techniques</h2>
                <ul style={{paddingLeft: '1.5rem', marginBottom: '2rem'}}>
                  {topic.importantPatterns.map((pattern, i) => <li key={i} style={{marginBottom: '0.5rem'}}>{pattern}</li>)}
                </ul>
              </>
            )}

            <h2 className="algo-viz-title">Pseudocode</h2>
            <div className="viz-code-block" style={{whiteSpace: 'pre-wrap', marginBottom: '2rem'}}>
              {topic.pseudocode}
            </div>

            <h2 className="algo-viz-title">Java Implementation</h2>
            <div className="viz-code-block" style={{whiteSpace: 'pre-wrap', marginBottom: '2rem'}}>
              {topic.codeImplementations?.java || '// No Java implementation available'}
            </div>

            <h2 className="algo-viz-title">C++ Implementation</h2>
            <div className="viz-code-block" style={{whiteSpace: 'pre-wrap', marginBottom: '2rem'}}>
              {topic.codeImplementations?.cpp || '// No C++ implementation available'}
            </div>
          </div>
        )}

        {activeTab === 'practice' && (
          <div className="algo-viz-panel animation-fade-in">
            <h2 className="algo-viz-title">Interactive MCQs</h2>
            <InteractiveQuiz questions={topic.mcqs} />

            <h2 className="algo-viz-title" style={{marginTop: '3rem'}}>Practice Questions</h2>
            <div style={{display: 'flex', gap: '1rem', marginBottom: '1.5rem', flexWrap: 'wrap'}}>
              <span className="viz-tag">🟢 Beginner</span>
              <span className="viz-tag" style={{borderColor: 'orange', color: 'orange'}}>🟡 Intermediate</span>
              <span className="viz-tag" style={{borderColor: 'red', color: 'red'}}>🔴 Advanced</span>
            </div>
            
            {topic.practiceQuestions?.map((q, i) => (
              <div key={i} className="algo-card" style={{marginBottom: '1.5rem'}}>
                <div className="algo-card-body">
                  <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem'}}>
                    <strong>Q{i + 1}.</strong>
                    <span className="viz-tag" style={{
                      borderColor: q.difficulty === 'BEGINNER' || q.difficulty === 'EASY' ? 'green' : q.difficulty === 'INTERMEDIATE' ? 'orange' : 'red',
                      color: q.difficulty === 'BEGINNER' || q.difficulty === 'EASY' ? 'green' : q.difficulty === 'INTERMEDIATE' ? 'orange' : 'red'
                    }}>{q.difficulty}</span>
                  </div>
                  <p style={{fontWeight: '500', fontSize: '1.1rem'}}>{q.prompt}</p>
                  <details style={{marginTop: '1rem', padding: '1rem', backgroundColor: 'var(--color-background)', borderRadius: 'var(--radius-sm)'}}>
                    <summary style={{cursor: 'pointer', fontWeight: 'bold'}}>View Solution & Explanation</summary>
                    <div className="viz-code-block" style={{marginTop: '1rem', whiteSpace: 'pre-wrap'}}>{q.solution}</div>
                    <p style={{marginTop: '1rem', lineHeight: '1.6'}}>{q.explanation}</p>
                  </details>
                </div>
              </div>
            ))}

            {topic.placementQuestions && topic.placementQuestions.length > 0 && (
              <>
                <h2 className="algo-viz-title" style={{marginTop: '3rem', color: 'var(--color-primary)'}}>💼 Placement-Level Problem Solving</h2>
                {topic.placementQuestions.map((q, i) => (
                  <div key={`pq-${i}`} className="algo-card" style={{marginBottom: '1.5rem', borderLeft: '4px solid var(--color-primary)'}}>
                    <div className="algo-card-body">
                      <p style={{fontWeight: 'bold', fontSize: '1.1rem', marginBottom: '1rem'}}>{q.prompt}</p>
                      <details style={{padding: '1rem', backgroundColor: 'var(--color-background)', borderRadius: 'var(--radius-sm)', marginBottom: '0.5rem'}}>
                        <summary style={{cursor: 'pointer', fontWeight: 'bold'}}>💡 Need a Hint?</summary>
                        <p style={{marginTop: '1rem'}}>{q.hints}</p>
                      </details>
                      <details style={{padding: '1rem', backgroundColor: 'var(--color-background)', borderRadius: 'var(--radius-sm)'}}>
                        <summary style={{cursor: 'pointer', fontWeight: 'bold'}}>View Detailed Solution</summary>
                        <div className="viz-code-block" style={{marginTop: '1rem', whiteSpace: 'pre-wrap'}}>{q.solution}</div>
                      </details>
                    </div>
                  </div>
                ))}
              </>
            )}

            <h2 className="algo-viz-title" style={{marginTop: '3rem'}}>Output-Based Questions</h2>
             {topic.outputBasedQuestions?.map((q, i) => (
              <div key={i} className="algo-card" style={{marginBottom: '1.5rem'}}>
                <div className="algo-card-body">
                  <strong>Code Snippet {i + 1}:</strong>
                  <div className="viz-code-block" style={{marginTop: '0.5rem', marginBottom: '1rem', whiteSpace: 'pre-wrap'}}>{q.code}</div>
                  <details style={{padding: '1rem', backgroundColor: 'var(--color-background)', borderRadius: 'var(--radius-sm)'}}>
                    <summary style={{cursor: 'pointer', fontWeight: 'bold'}}>View Output & Explanation</summary>
                    <div className="viz-code-block" style={{marginTop: '1rem'}}>{q.output}</div>
                    <p style={{marginTop: '1rem'}}>{q.explanation}</p>
                  </details>
                </div>
              </div>
            ))}
          </div>
        )}

        {activeTab === 'interview' && (
          <div className="algo-viz-panel animation-fade-in">
            <h2 className="algo-viz-title">Common Interview Questions</h2>
            {topic.interviewQuestions?.map((q, i) => (
              <div key={i} className="algo-card" style={{marginBottom: '1.5rem'}}>
                <div className="algo-card-body">
                  <h4 style={{marginBottom: '1rem'}}>{q.question}</h4>
                  <details style={{padding: '1rem', backgroundColor: 'var(--color-background)', borderRadius: 'var(--radius-sm)'}}>
                    <summary style={{cursor: 'pointer', fontWeight: 'bold', color: 'var(--color-primary)'}}>Show Answer</summary>
                    <p style={{marginTop: '1rem', whiteSpace: 'pre-wrap'}}>{q.answer}</p>
                    {q.explanation && (
                      <div style={{marginTop: '1rem', paddingLeft: '1rem', borderLeft: '3px solid var(--color-primary)'}}>
                        <strong style={{display: 'block', marginBottom: '0.5rem'}}>Deep Dive / Explanation:</strong>
                        <p style={{whiteSpace: 'pre-wrap'}}>{q.explanation}</p>
                      </div>
                    )}
                  </details>
                </div>
              </div>
            ))}

            {topic.deepDiveInterviewQuestions && topic.deepDiveInterviewQuestions.length > 0 && (
              <>
                <h2 className="algo-viz-title" style={{marginTop: '3rem'}}>Deep Dive Questions</h2>
                {topic.deepDiveInterviewQuestions.map((q, i) => (
                  <div key={`dd-${i}`} className="algo-card" style={{marginBottom: '1.5rem'}}>
                    <div className="algo-card-body">
                      <h4 style={{marginBottom: '1rem', color: 'var(--color-primary)'}}>{q.question}</h4>
                      <details style={{padding: '1rem', backgroundColor: 'var(--color-background)', borderRadius: 'var(--radius-sm)'}}>
                        <summary style={{cursor: 'pointer', fontWeight: 'bold'}}>Show Detailed Answer</summary>
                        <p style={{marginTop: '1rem', whiteSpace: 'pre-wrap'}}>{q.answer}</p>
                      </details>
                    </div>
                  </div>
                ))}
              </>
            )}

            {topic.comparisonQuestions && topic.comparisonQuestions.length > 0 && (
              <>
                <h2 className="algo-viz-title" style={{marginTop: '3rem'}}>Comparison & Trade-offs</h2>
                {topic.comparisonQuestions.map((q, i) => (
                  <div key={`cq-${i}`} className="algo-card" style={{marginBottom: '1.5rem'}}>
                    <div className="algo-card-body">
                      <h4 style={{marginBottom: '1rem'}}>{q.question}</h4>
                      <details style={{padding: '1rem', backgroundColor: 'var(--color-background)', borderRadius: 'var(--radius-sm)'}}>
                        <summary style={{cursor: 'pointer', fontWeight: 'bold'}}>Show Comparison</summary>
                        <p style={{marginTop: '1rem', whiteSpace: 'pre-wrap'}}>{q.answer}</p>
                      </details>
                    </div>
                  </div>
                ))}
              </>
            )}

            {topic.conceptualQuestions && topic.conceptualQuestions.length > 0 && (
              <>
                <h2 className="algo-viz-title" style={{marginTop: '3rem'}}>Conceptual: "Why does this work?"</h2>
                {topic.conceptualQuestions.map((q, i) => (
                  <div key={`conc-${i}`} className="algo-card" style={{marginBottom: '1.5rem'}}>
                    <div className="algo-card-body">
                      <h4 style={{marginBottom: '1rem'}}>{q.question}</h4>
                      <details style={{padding: '1rem', backgroundColor: 'var(--color-background)', borderRadius: 'var(--radius-sm)'}}>
                        <summary style={{cursor: 'pointer', fontWeight: 'bold'}}>Show Conceptual Insight</summary>
                        <p style={{marginTop: '1rem', whiteSpace: 'pre-wrap'}}>{q.answer}</p>
                      </details>
                    </div>
                  </div>
                ))}
              </>
            )}

            <h2 className="algo-viz-title" style={{marginTop: '3rem'}}>Common Mistakes & Pitfalls</h2>
            <ul style={{paddingLeft: '1.5rem', marginBottom: '2rem'}}>
              {topic.commonMistakes?.map((mistake, i) => (
                <li key={i} style={{marginBottom: '0.5rem'}}>{mistake}</li>
              ))}
            </ul>
          </div>
        )}

      </div>
    </div>
  );
};

export default TopicDetailPage;
