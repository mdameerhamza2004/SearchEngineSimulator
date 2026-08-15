import React, { useState } from 'react';

const InteractiveQuiz = ({ questions }) => {
  const [selectedAnswers, setSelectedAnswers] = useState({});

  if (!questions || questions.length === 0) return null;

  const handleSelect = (idx, option) => {
    setSelectedAnswers(prev => ({
      ...prev,
      [idx]: option
    }));
  };

  return (
    <div className="interactive-quiz">
      {questions.map((q, idx) => {
        const isAnswered = selectedAnswers[idx] !== undefined;
        const selectedOption = selectedAnswers[idx];
        const isCorrect = selectedOption === q.correctOption;

        return (
          <div key={idx} className="algo-card" style={{marginBottom: '1.5rem'}}>
            <div className="algo-card-body">
              <h4 className="algo-card-title" style={{marginBottom: '1rem'}}>
                Q{idx + 1}. {q.question}
              </h4>
              <div style={{display: 'flex', flexDirection: 'column', gap: '0.5rem'}}>
                {q.options.map((opt, i) => {
                  let styleClass = "viz-btn";
                  if (isAnswered) {
                    if (opt === q.correctOption) styleClass += " primary";
                    else if (opt === selectedOption) styleClass += " secondary";
                    else styleClass += " disabled";
                  }

                  return (
                    <button 
                      key={i} 
                      className={styleClass}
                      onClick={() => !isAnswered && handleSelect(idx, opt)}
                      disabled={isAnswered}
                      style={{textAlign: 'left', display: 'block', width: '100%'}}
                    >
                      {opt}
                    </button>
                  );
                })}
              </div>

              {isAnswered && (
                <div style={{
                  marginTop: '1rem', 
                  padding: '1rem', 
                  backgroundColor: isCorrect ? 'rgba(16, 185, 129, 0.1)' : 'rgba(239, 68, 68, 0.1)',
                  borderRadius: 'var(--radius-md)',
                  border: `1px solid ${isCorrect ? '#10b981' : '#ef4444'}`
                }}>
                  <strong style={{color: isCorrect ? '#10b981' : '#ef4444', display: 'block', marginBottom: '0.5rem'}}>
                    {isCorrect ? 'Correct!' : 'Incorrect.'}
                  </strong>
                  <div>{q.explanation}</div>
                </div>
              )}
            </div>
          </div>
        );
      })}
    </div>
  );
};

export default InteractiveQuiz;
