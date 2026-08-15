import React, { useState, useCallback } from 'react';
import { Search, ChevronLeft, ChevronRight, RotateCcw } from 'lucide-react';
import { apiClient } from '../../api/apiClient';

/* -----------------------------------------------------------------------
   TOP-K / PRIORITY QUEUE VISUALIZER
   Uses TF-IDF document scores as candidates, simulates min-heap insertion
   step by step in the browser (the O(N log K) simulation is client-side
   but uses real scores from the backend).
----------------------------------------------------------------------- */

function buildHeapSteps(candidates, k) {
  // Simulates a min-heap of size K over N candidates
  // Returns array of step objects for animation
  const steps = [];
  const heap = []; // min-heap: heap[0] is minimum

  const heapify = (arr, i) => {
    let smallest = i;
    const l = 2 * i + 1, r = 2 * i + 2;
    if (l < arr.length && arr[l].score < arr[smallest].score) smallest = l;
    if (r < arr.length && arr[r].score < arr[smallest].score) smallest = r;
    if (smallest !== i) {
      [arr[i], arr[smallest]] = [arr[smallest], arr[i]];
      heapify(arr, smallest);
    }
  };

  const bubbleUp = (arr, i) => {
    while (i > 0) {
      const parent = Math.floor((i - 1) / 2);
      if (arr[parent].score > arr[i].score) {
        [arr[i], arr[parent]] = [arr[parent], arr[i]];
        i = parent;
      } else break;
    }
  };

  candidates.forEach((cand, idx) => {
    const prevHeap = heap.map(h => ({ ...h }));
    if (heap.length < k) {
      heap.push({ ...cand });
      bubbleUp(heap, heap.length - 1);
      steps.push({
        type: 'insert',
        candidate: cand,
        action: `Insert "${cand.title.substring(0, 30)}…" (score ${cand.score.toFixed(4)}) — heap has room (${heap.length}/${k})`,
        heap: heap.map(h => ({ ...h })),
        evicted: null,
      });
    } else if (cand.score > heap[0].score) {
      const evicted = { ...heap[0] };
      heap[0] = { ...cand };
      heapify(heap, 0);
      steps.push({
        type: 'replace',
        candidate: cand,
        action: `Replace minimum "${evicted.title.substring(0, 25)}…" (${evicted.score.toFixed(4)}) with "${cand.title.substring(0, 25)}…" (${cand.score.toFixed(4)})`,
        heap: heap.map(h => ({ ...h })),
        evicted,
      });
    } else {
      steps.push({
        type: 'skip',
        candidate: cand,
        action: `Skip "${cand.title.substring(0, 30)}…" (score ${cand.score.toFixed(4)}) — less than heap min ${heap[0]?.score.toFixed(4)}`,
        heap: heap.map(h => ({ ...h })),
        evicted: null,
      });
    }
  });

  // Final step
  steps.push({
    type: 'done',
    action: `Done — Top-${k} results sorted by score`,
    heap: [...heap].sort((a, b) => b.score - a.score),
    candidate: null,
    evicted: null,
  });

  return steps;
}

export default function TopKVisualizer() {
  const [inputValue, setInputValue] = useState('');
  const [k, setK] = useState(3);
  const [data, setData] = useState(null);
  const [steps, setSteps] = useState([]);
  const [step, setStep] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchData = useCallback(async (q) => {
    if (!q.trim()) return;
    setLoading(true);
    setError(null);
    try {
      const res = await apiClient.getTfIdfVisualization(q);
      setData(res);
      const candidates = res.documentScores.map(d => ({ ...d, title: d.title }));
      const newSteps = buildHeapSteps(candidates, k);
      setSteps(newSteps);
      setStep(0);
    } catch (e) {
      setError('Failed to load data. Is the backend running?');
    } finally {
      setLoading(false);
    }
  }, [k]);

  const handleSearch = () => fetchData(inputValue.trim());
  const handleReset = () => { setInputValue(''); setData(null); setSteps([]); setStep(0); setError(null); };

  const currentStep = steps[step];

  const typeColor = { insert: '#22c55e', replace: '#f59e0b', skip: '#6b7280', done: 'var(--color-primary)' };
  const typeLabel = { insert: 'INSERT', replace: 'REPLACE MIN', skip: 'SKIP', done: 'FINAL' };

  return (
    <div className="viz-container">
      <div className="viz-controls-row">
        <input
          className="viz-input"
          placeholder="Enter a query (e.g. binary search)"
          value={inputValue}
          onChange={e => setInputValue(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && handleSearch()}
        />
        <select
          className="viz-select"
          value={k}
          onChange={e => setK(Number(e.target.value))}
        >
          {[2,3,4,5,7,10].map(v => <option key={v} value={v}>K = {v}</option>)}
        </select>
        <button className="viz-btn primary" onClick={handleSearch}>
          <Search size={16} /> Simulate
        </button>
        <button className="viz-btn" onClick={handleReset}>
          <RotateCcw size={16} /> Reset
        </button>
      </div>

      {loading && <div className="viz-loading">Loading candidates…</div>}
      {error && <div className="viz-error">{error}</div>}

      {steps.length > 0 && currentStep && (
        <>
          <div className="viz-step-controls">
            <button className="viz-btn" onClick={() => setStep(s => Math.max(0, s - 1))} disabled={step === 0}>
              <ChevronLeft size={16} /> Prev
            </button>
            <span className="viz-step-label">Step {step + 1} / {steps.length}</span>
            <button className="viz-btn" onClick={() => setStep(s => Math.min(steps.length - 1, s + 1))} disabled={step === steps.length - 1}>
              Next <ChevronRight size={16} />
            </button>
          </div>

          {/* Current step action */}
          <div className="topk-action" style={{ borderColor: typeColor[currentStep.type] }}>
            <span className="topk-action-badge" style={{ background: typeColor[currentStep.type] }}>
              {typeLabel[currentStep.type]}
            </span>
            <span className="topk-action-text">{currentStep.action}</span>
          </div>

          {/* Heap visualization */}
          <div className="topk-heap-section">
            <div className="topk-heap-label">
              Min-Heap State (K={k}) — Root = Minimum
            </div>
            <div className="topk-heap">
              {currentStep.heap.length === 0 ? (
                <div className="viz-muted">Empty heap</div>
              ) : (
                currentStep.heap.map((item, i) => (
                  <div
                    key={item.docId}
                    className={`topk-heap-item ${i === 0 && currentStep.heap.length >= k ? 'heap-min' : ''}`}
                    style={{ '--rank': i }}
                  >
                    <div className="topk-heap-rank">
                      {i === 0 && currentStep.heap.length >= k ? '⚡ min' : `[${i}]`}
                    </div>
                    <div className="topk-heap-title">{item.title.substring(0, 28)}{item.title.length > 28 ? '…' : ''}</div>
                    <div className="topk-heap-score">{item.finalScore?.toFixed(4) ?? item.score?.toFixed(4)}</div>
                  </div>
                ))
              )}
            </div>
          </div>

          {/* Complexity callout */}
          <div className="topk-complexity">
            <strong>Why O(N log K) beats O(N log N)?</strong>
            <p>Sorting all {data?.documentScores.length ?? 'N'} candidates takes O(N log N). A min-heap of size K only costs O(log K) per candidate → <strong>O(N log K)</strong> total. When K ≪ N, this is significantly faster.</p>
          </div>
        </>
      )}

      {!data && !loading && (
        <div className="viz-placeholder">
          Enter a query to simulate the Top-K min-heap selection algorithm.
          Try: <code>binary search</code>, <code>sorting algorithms</code>
        </div>
      )}
    </div>
  );
}
