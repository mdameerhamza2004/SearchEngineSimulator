import React, { useState, useCallback } from 'react';
import { ChevronLeft, ChevronRight, RotateCcw, Play } from 'lucide-react';
import { apiClient } from '../../api/apiClient';

/* -----------------------------------------------------------------------
   LEVENSHTEIN VISUALIZER
   Interactive DP matrix from real backend LevenshteinDistance class
----------------------------------------------------------------------- */

const OP_COLORS = {
  MATCH: { bg: '#dcfce7', border: '#22c55e', text: '#166534' },
  SUBSTITUTE: { bg: '#fef3c7', border: '#f59e0b', text: '#92400e' },
  INSERT: { bg: '#dbeafe', border: '#3b82f6', text: '#1e40af' },
  DELETE: { bg: '#fee2e2', border: '#ef4444', text: '#991b1b' },
};

function backtrackPath(matrix, source, target) {
  if (!matrix || source.length === 0 && target.length === 0) return new Set();
  const path = new Set();
  let i = source.length, j = target.length;
  while (i > 0 || j > 0) {
    path.add(`${i},${j}`);
    if (i > 0 && j > 0 && source[i-1] === target[j-1]) {
      i--; j--;
    } else if (i > 0 && j > 0 && matrix[i][j] === matrix[i-1][j-1] + 1) {
      i--; j--;
    } else if (j > 0 && matrix[i][j] === matrix[i][j-1] + 1) {
      j--;
    } else if (i > 0) {
      i--;
    }
  }
  path.add('0,0');
  return path;
}

function getOpForCell(matrix, source, target, i, j) {
  if (i === 0 && j === 0) return null;
  if (i === 0) return 'INSERT';
  if (j === 0) return 'DELETE';
  if (source[i-1] === target[j-1]) return 'MATCH';
  if (matrix[i][j] === matrix[i-1][j-1] + 1) return 'SUBSTITUTE';
  if (matrix[i][j] === matrix[i][j-1] + 1) return 'INSERT';
  if (matrix[i][j] === matrix[i-1][j] + 1) return 'DELETE';
  return null;
}

export default function LevenshteinVisualizer() {
  const [source, setSource] = useState('');
  const [target, setTarget] = useState('');
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [step, setStep] = useState(-1); // -1 = show full matrix
  const [highlightPath, setHighlightPath] = useState(true);

  const fetchData = useCallback(async (s, t) => {
    if (!s.trim() || !t.trim()) return;
    setLoading(true);
    setError(null);
    try {
      const res = await apiClient.getLevenshteinVisualization(s, t);
      setData(res);
      setStep(-1);
    } catch (e) {
      setError('Failed to compute Levenshtein distance. Is the backend running?');
    } finally {
      setLoading(false);
    }
  }, []);

  const handleCompute = () => fetchData(source.trim().toLowerCase(), target.trim().toLowerCase());
  const handleReset = () => { setSource(''); setTarget(''); setData(null); setStep(-1); setError(null); };

  const optimalPath = data ? backtrackPath(data.matrix, data.source, data.target) : new Set();
  const maxStep = data ? (data.source.length + 1) * (data.target.length + 1) - 1 : 0;
  const currentCellCount = step === -1 ? maxStep + 1 : step + 1;

  return (
    <div className="viz-container">
      <div className="viz-controls-row">
        <input
          className="viz-input"
          placeholder="Source word (e.g. algoritm)"
          value={source}
          onChange={e => setSource(e.target.value.slice(0, 20))}
          onKeyDown={e => e.key === 'Enter' && handleCompute()}
          style={{ maxWidth: 180 }}
        />
        <span style={{ color: 'var(--color-text-muted)', fontSize: '1.2rem' }}>→</span>
        <input
          className="viz-input"
          placeholder="Target word (e.g. algorithm)"
          value={target}
          onChange={e => setTarget(e.target.value.slice(0, 20))}
          onKeyDown={e => e.key === 'Enter' && handleCompute()}
          style={{ maxWidth: 180 }}
        />
        <button className="viz-btn primary" onClick={handleCompute}>
          <Play size={16} /> Compute
        </button>
        <button className="viz-btn" onClick={handleReset}>
          <RotateCcw size={16} /> Reset
        </button>
      </div>

      {loading && <div className="viz-loading">Computing DP matrix…</div>}
      {error && <div className="viz-error">{error}</div>}

      {data && (
        <>
          <div className="viz-stats-row">
            <span className="viz-stat">
              <strong>"{data.source}"</strong> → <strong>"{data.target}"</strong>
            </span>
            <span className="viz-stat lev-distance">
              Edit Distance = <strong>{data.editDistance}</strong>
            </span>
          </div>

          <div className="viz-step-controls">
            <button className="viz-btn" onClick={() => setStep(s => Math.max(0, s === -1 ? maxStep - 1 : s - 1))}>
              <ChevronLeft size={16} /> Prev
            </button>
            <span className="viz-step-label">
              {step === -1 ? 'Full Matrix' : `Filling cell ${step + 1} / ${maxStep + 1}`}
            </span>
            <button className="viz-btn" onClick={() => setStep(s => s >= maxStep ? -1 : s + 1)}>
              Next <ChevronRight size={16} />
            </button>
            <button className="viz-btn" onClick={() => setStep(-1)}>Show All</button>
          </div>

          <div className="lev-matrix-wrapper">
            <table className="lev-matrix">
              <thead>
                <tr>
                  <th className="lev-header-cell" />
                  <th className="lev-header-cell">ε</th>
                  {data.target.split('').map((c, j) => (
                    <th key={j} className="lev-header-cell">{c}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {data.matrix.map((row, i) => (
                  <tr key={i}>
                    <th className="lev-header-cell">{i === 0 ? 'ε' : data.source[i-1]}</th>
                    {row.map((val, j) => {
                      const cellIndex = i * (data.target.length + 1) + j;
                      const visible = step === -1 || cellIndex <= step;
                      const inPath = highlightPath && optimalPath.has(`${i},${j}`);
                      const op = getOpForCell(data.matrix, data.source, data.target, i, j);
                      const colors = inPath && op ? OP_COLORS[op] : null;

                      return (
                        <td
                          key={j}
                          className={`lev-cell ${inPath ? 'lev-cell-path' : ''} ${!visible ? 'lev-cell-hidden' : ''}`}
                          style={colors ? { background: colors.bg, borderColor: colors.border, color: colors.text, fontWeight: 700 } : {}}
                          title={inPath && op ? op : ''}
                        >
                          {visible ? val : '·'}
                        </td>
                      );
                    })}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <div className="lev-legend">
            {Object.entries(OP_COLORS).map(([op, c]) => (
              <span key={op} className="lev-legend-item">
                <span className="lev-legend-dot" style={{ background: c.bg, border: `2px solid ${c.border}` }} />
                {op}
              </span>
            ))}
            <label className="lev-path-toggle">
              <input type="checkbox" checked={highlightPath} onChange={e => setHighlightPath(e.target.checked)} />
              Highlight optimal path
            </label>
          </div>

          {/* Operations list */}
          {data.operations.length > 0 && (
            <div className="lev-ops">
              <div className="lev-ops-title">Edit Operations (optimal path):</div>
              <div className="lev-ops-list">
                {data.operations.map((op, i) => {
                  const type = op.split(':')[0];
                  const colors = OP_COLORS[type];
                  return (
                    <span
                      key={i}
                      className="lev-op-badge"
                      style={colors ? { background: colors.bg, borderColor: colors.border, color: colors.text } : {}}
                    >
                      {op}
                    </span>
                  );
                })}
              </div>
            </div>
          )}
        </>
      )}

      {!data && !loading && (
        <div className="viz-placeholder">
          Enter two words to compute Levenshtein edit distance with full DP matrix.
          Try: <code>algoritm</code> → <code>algorithm</code>, or <code>kitten</code> → <code>sitting</code>
        </div>
      )}
    </div>
  );
}
