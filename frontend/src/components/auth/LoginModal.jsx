import React, { useState, useEffect, useCallback, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, X } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import './LoginModal.css';

/* ======================================================================
   LOGIN MODAL
   ====================================================================== */
export default function LoginModal({ onClose }) {
  const { login, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const [email, setEmail]       = useState('');
  const [password, setPassword] = useState('');
  const [error, setError]       = useState(null);
  const [loading, setLoading]   = useState(false);

  const panelRef = useRef(null);

  // Close on Escape
  useEffect(() => {
    const handler = (e) => { if (e.key === 'Escape') onClose(); };
    document.addEventListener('keydown', handler);
    return () => document.removeEventListener('keydown', handler);
  }, [onClose]);

  // Auto-close once user authenticates
  useEffect(() => {
    if (isAuthenticated) onClose();
  }, [isAuthenticated, onClose]);

  // Focus first focusable element on open (accessibility)
  useEffect(() => {
    const el = panelRef.current;
    if (!el) return;
    const focusable = el.querySelectorAll('input, button, a, [tabindex]:not([tabindex="-1"])');
    if (focusable.length) focusable[0].focus();
  }, []);

  // Close on backdrop click only
  const handleBackdropClick = useCallback((e) => {
    if (e.target === e.currentTarget) onClose();
  }, [onClose]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await login(email, password);
      // onClose fires from the isAuthenticated useEffect
    } catch (err) {
      setError(err.message || 'Invalid email or password.');
    } finally {
      setLoading(false);
    }
  };

  const goTo = (path) => {
    onClose();
    navigate(path);
  };

  return (
    <div
      className="lm-backdrop"
      onClick={handleBackdropClick}
      role="dialog"
      aria-modal="true"
      aria-label="Login"
    >
      <div className="lm-panel" ref={panelRef}>

        {/* Close button */}
        <button className="lm-close" onClick={onClose} aria-label="Close login dialog" title="Close (Esc)">
          <X size={16} />
        </button>

        {/* Header */}
        <div className="lm-header">
          <div className="lm-logo">
            <Search size={18} />
            SearchEngineSim
          </div>
          <div className="lm-title">Welcome back</div>
          <div className="lm-subtitle">Sign in to save your work and unlock all features</div>
        </div>

        {/* Form body */}
        <div className="lm-body">

          {error && (
            <div className="lm-error" role="alert">
              <span>⚠️</span> {error}
            </div>
          )}

          {/* Login form */}
          <form onSubmit={handleSubmit} noValidate>
            <div className="lm-field">
              <label className="lm-label" htmlFor="lm-email">Email</label>
              <input
                id="lm-email"
                type="email"
                className="lm-input"
                placeholder="you@example.com"
                value={email}
                onChange={e => setEmail(e.target.value)}
                required
                autoComplete="email"
              />
            </div>

            <div className="lm-field">
              <label className="lm-label" htmlFor="lm-password">Password</label>
              <input
                id="lm-password"
                type="password"
                className="lm-input"
                placeholder="Your password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                required
                autoComplete="current-password"
              />
            </div>

            <button type="submit" className="lm-btn-primary" disabled={loading}>
              {loading && <span className="lm-spinner" aria-hidden="true" />}
              {loading ? 'Signing in…' : 'Sign In'}
            </button>
          </form>

          {/* Forgot password */}
          <div className="lm-links">
            <button onClick={() => goTo('/forgot-password')}>Forgot password?</button>
          </div>

          {/* Divider */}
          <div className="lm-divider">or</div>

          {/* Google Login */}
          <a
            href="http://localhost:8080/oauth2/authorization/google"
            className="lm-btn-google"
            aria-label="Sign in with Google"
          >
            <svg className="lm-google-icon" viewBox="0 0 24 24" aria-hidden="true">
              <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
              <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
              <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
              <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
            </svg>
            Continue with Google
          </a>

          {/* Register */}
          <div className="lm-register-row">
            New to SearchEngineSim?{' '}
            <button onClick={() => goTo('/register')}>Create a free account</button>
          </div>

          {/* Continue as guest */}
          <button className="lm-guest-banner" onClick={onClose} aria-label="Continue as guest">
            👤 Continue as guest — explore public features without an account
          </button>

        </div>
      </div>
    </div>
  );
}
