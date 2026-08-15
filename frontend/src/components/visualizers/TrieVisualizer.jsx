import React, { useState, useCallback } from 'react';
import { Search, ChevronLeft, ChevronRight, RotateCcw } from 'lucide-react';
import { apiClient } from '../../api/apiClient';

/* -----------------------------------------------------------------------
   TRIE VISUALIZER
   Renders the Trie subtree as an SVG tree.
   Uses real backend data from /api/visualization/trie
----------------------------------------------------------------------- */

const NODE_R = 18;
const H_GAP = 48;
const V_GAP = 70;

function buildLayout(nodes, edges) {
  if (!nodes || nodes.length === 0) return { positions: {}, width: 400, height: 200 };

  // Build adjacency
  const childrenMap = {};
  const parentMap = {};
  edges.forEach(e => {
    if (!childrenMap[e.fromId]) childrenMap[e.fromId] = [];
    childrenMap[e.fromId].push(e.toId);
    parentMap[e.toId] = e.fromId;
  });

  // Find root
  const allIds = nodes.map(n => n.id);
  const childIds = new Set(Object.values(parentMap));
  const rootId = allIds.find(id => !childIds.has(id)) || 'ROOT';

  // Assign x positions via post-order traversal (Reingold-Tilford simplified)
  const positions = {};
  let maxDepth = 0;

  function assignX(nodeId, depth) {
    maxDepth = Math.max(maxDepth, depth);
    const children = childrenMap[nodeId] || [];
    if (children.length === 0) {
      positions[nodeId] = { x: 0, y: depth * V_GAP + NODE_R + 10, depth };
      return 0;
    }
    const childXs = children.map(c => assignX(c, depth + 1));
    const leftmost = Math.min(...childXs);
    const rightmost = Math.max(...childXs);
    const cx = (leftmost + rightmost) / 2;
    positions[nodeId] = { x: cx, y: depth * V_GAP + NODE_R + 10, depth };
    return cx;
  }

  // First pass: compute relative positions
  function computeSubtreeWidth(nodeId) {
    const children = (childrenMap[nodeId] || []).sort();
    if (children.length === 0) return 1;
    return children.reduce((sum, c) => sum + computeSubtreeWidth(c), 0);
  }

  let xCursor = 0;
  function assignAbsoluteX(nodeId, depth) {
    const children = (childrenMap[nodeId] || []).sort();
    if (children.length === 0) {
      positions[nodeId] = { x: xCursor * H_GAP + NODE_R + 10, y: depth * V_GAP + NODE_R + 10 };
      xCursor++;
      return;
    }
    const startX = xCursor;
    children.forEach(c => assignAbsoluteX(c, depth + 1));
    const endX = xCursor;
    positions[nodeId] = {
      x: ((startX + endX - 1) / 2) * H_GAP + NODE_R + 10,
      y: depth * V_GAP + NODE_R + 10
    };
  }

  assignAbsoluteX(rootId, 0);

  const allX = Object.values(positions).map(p => p.x);
  const allY = Object.values(positions).map(p => p.y);
  const width = Math.max(...allX) + NODE_R + 20;
  const height = Math.max(...allY) + NODE_R + 20;

  return { positions, width, height, rootId };
}

export default function TrieVisualizer() {
  const [prefix, setPrefix] = useState('');
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [step, setStep] = useState(0); // 0..prefix.length for animation
  const [inputValue, setInputValue] = useState('');

  const fetchData = useCallback(async (p) => {
    setLoading(true);
    setError(null);
    try {
      const res = await apiClient.getTrieVisualization(p);
      setData(res);
      setStep(0);
    } catch (e) {
      setError('Failed to load Trie data. Is the backend running?');
    } finally {
      setLoading(false);
    }
  }, []);

  const handleSearch = () => {
    const p = inputValue.trim().toLowerCase();
    setPrefix(p);
    fetchData(p);
  };

  const handleReset = () => {
    setInputValue('');
    setPrefix('');
    setData(null);
    setStep(0);
    setError(null);
  };

  // Current traversal is up to `step` characters of prefix
  const currentPathPrefix = prefix.slice(0, step);
  const maxSteps = prefix.length;

  // Determine which node IDs are "active" for current step
  const activeIds = new Set();
  if (data) {
    activeIds.add('ROOT');
    let cur = 'ROOT';
    for (let i = 0; i < step; i++) {
      cur = cur + '-' + prefix[i];
      activeIds.add(cur);
    }
  }

  const layout = data ? buildLayout(data.nodes, data.edges) : null;

  return (
    <div className="viz-container">
      <div className="viz-controls-row">
        <input
          className="viz-input"
          placeholder="Enter prefix (e.g. pro, bin, alg)"
          value={inputValue}
          onChange={e => setInputValue(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && handleSearch()}
        />
        <button className="viz-btn primary" onClick={handleSearch}>
          <Search size={16} /> Visualize
        </button>
        <button className="viz-btn" onClick={handleReset}>
          <RotateCcw size={16} /> Reset
        </button>
      </div>

      {loading && <div className="viz-loading">Loading Trie data…</div>}
      {error && <div className="viz-error">{error}</div>}

      {data && (
        <>
          <div className="viz-stats-row">
            <span className="viz-stat"><strong>{data.totalWords}</strong> words in Trie</span>
            <span className="viz-stat"><strong>{data.nodes.length}</strong> nodes shown</span>
            {data.suggestions.length > 0 && (
              <span className="viz-stat">
                Suggestions: {data.suggestions.map((s, i) => (
                  <span key={i} className="viz-tag">{s}</span>
                ))}
              </span>
            )}
          </div>

          {prefix && (
            <div className="viz-step-controls">
              <button className="viz-btn" onClick={() => setStep(s => Math.max(0, s - 1))} disabled={step === 0}>
                <ChevronLeft size={16} /> Prev
              </button>
              <span className="viz-step-label">
                Step {step}/{maxSteps} — Traversing: <code>{currentPathPrefix || 'ROOT'}</code>
              </span>
              <button className="viz-btn" onClick={() => setStep(s => Math.min(maxSteps, s + 1))} disabled={step === maxSteps}>
                Next <ChevronRight size={16} />
              </button>
            </div>
          )}

          <div className="viz-svg-scroll">
            <svg width={Math.max(layout.width, 400)} height={Math.max(layout.height, 200)}
              style={{ display: 'block', margin: '0 auto' }}>
              {/* Edges */}
              {data.edges.map((edge, i) => {
                const from = layout.positions[edge.fromId];
                const to = layout.positions[edge.toId];
                if (!from || !to) return null;
                const isActive = activeIds.has(edge.fromId) && activeIds.has(edge.toId);
                return (
                  <g key={i}>
                    <line
                      x1={from.x} y1={from.y} x2={to.x} y2={to.y}
                      stroke={isActive ? 'var(--color-primary)' : 'var(--color-border)'}
                      strokeWidth={isActive ? 2.5 : 1.5}
                    />
                    <text
                      x={(from.x + to.x) / 2 - 8}
                      y={(from.y + to.y) / 2}
                      fontSize="11" fill="var(--color-text-muted)"
                      fontFamily="monospace"
                    >
                      {edge.character}
                    </text>
                  </g>
                );
              })}
              {/* Nodes */}
              {data.nodes.map((node, i) => {
                const pos = layout.positions[node.id];
                if (!pos) return null;
                const isActive = activeIds.has(node.id);
                const isEnd = node.isEndOfWord;
                const fill = isActive
                  ? 'var(--color-primary)'
                  : isEnd ? 'var(--color-surface)' : 'var(--color-surface)';
                const stroke = isActive ? 'var(--color-primary)'
                  : isEnd ? '#22c55e' : 'var(--color-border)';
                const textColor = isActive ? '#fff' : 'var(--color-text)';
                return (
                  <g key={i}>
                    <circle
                      cx={pos.x} cy={pos.y} r={NODE_R}
                      fill={fill} stroke={stroke} strokeWidth={isEnd ? 2.5 : 1.5}
                    />
                    {isEnd && !isActive && (
                      <circle cx={pos.x} cy={pos.y} r={NODE_R - 4}
                        fill="none" stroke="#22c55e" strokeWidth={1} />
                    )}
                    <text
                      x={pos.x} y={pos.y + 5}
                      textAnchor="middle" fontSize="13"
                      fill={textColor} fontFamily="monospace" fontWeight="600"
                    >
                      {node.id === 'ROOT' ? '·' : node.character}
                    </text>
                    {isEnd && node.word && (
                      <text
                        x={pos.x} y={pos.y + NODE_R + 14}
                        textAnchor="middle" fontSize="10"
                        fill="#22c55e" fontFamily="monospace"
                      >
                        {node.word}
                      </text>
                    )}
                  </g>
                );
              })}
            </svg>
          </div>

          <div className="viz-legend">
            <span className="legend-item"><span className="legend-dot active" />Traversal path</span>
            <span className="legend-item"><span className="legend-dot end-of-word" />End of word</span>
            <span className="legend-item"><span className="legend-dot normal" />Internal node</span>
          </div>
        </>
      )}

      {!data && !loading && (
        <div className="viz-placeholder">
          Enter a prefix above to explore the Trie structure. Try: <code>pro</code>, <code>bin</code>, <code>alg</code>
        </div>
      )}
    </div>
  );
}
