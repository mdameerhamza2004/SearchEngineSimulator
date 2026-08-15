import React, { useState, useEffect, useCallback } from 'react';
import { BrowserRouter, Routes, Route, NavLink, useLocation } from 'react-router-dom';
import { Moon, Sun, Search as SearchIcon, GitBranch, LogIn, LogOut, User } from 'lucide-react';
import './index.css';

import SearchPage from './components/SearchPage';
import AlgorithmsPage from './pages/AlgorithmsPage';
import AnalyticsPage from './pages/AnalyticsPage';
import DocumentsPage from './pages/DocumentsPage';
import LearningHubPage from './pages/LearningHubPage';
import TopicDetailPage from './pages/TopicDetailPage';

import { AuthProvider, useAuth } from './context/AuthContext';
import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';
import ForgotPasswordPage from './pages/auth/ForgotPasswordPage';
import ResetPasswordPage from './pages/auth/ResetPasswordPage';
import AuthEducationPage from './pages/auth/AuthEducationPage';
import LoginModal from './components/auth/LoginModal';

/* ======================================================================
   ERROR BOUNDARY
   ====================================================================== */
class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }
  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }
  componentDidCatch(error, errorInfo) {
    console.error('ErrorBoundary caught an error', error, errorInfo);
  }
  render() {
    if (this.state.hasError) {
      return (
        <div style={{ padding: '2rem', color: '#ef4444' }}>
          <h1>⚠ Render Error: {this.state.error?.message}</h1>
          <pre style={{ fontSize: '0.75rem', marginTop: '1rem', opacity: 0.7 }}>{this.state.error?.stack}</pre>
        </div>
      );
    }
    return this.props.children;
  }
}

/* ======================================================================
   NAVBAR AUTH SECTION — shows Login button or Welcome + Logout
   ====================================================================== */
function NavAuthLinks({ onOpenModal }) {
  const { user, isAuthenticated, logout } = useAuth();

  if (isAuthenticated) {
    return (
      <>
        <span className="nav-link" style={{ color: 'var(--color-primary)', fontWeight: 600, display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
          <User size={14} />
          {user?.name || user?.email || 'User'}
        </span>
        <button
          className="nav-link"
          onClick={logout}
          style={{ background: 'none', border: 'none', cursor: 'pointer', fontFamily: 'inherit', fontSize: 'inherit', display: 'flex', alignItems: 'center', gap: '0.35rem', color: 'var(--color-text-muted)' }}
          title="Logout"
        >
          <LogOut size={14} /> Logout
        </button>
      </>
    );
  }

  return (
    <button
      className="nav-link"
      onClick={onOpenModal}
      style={{ background: 'none', border: 'none', cursor: 'pointer', fontFamily: 'inherit', fontSize: 'inherit', display: 'flex', alignItems: 'center', gap: '0.35rem', color: 'var(--color-primary)', fontWeight: 600 }}
      title="Login or Register"
    >
      <LogIn size={14} /> Login
    </button>
  );
}

/* ======================================================================
   AUTO-SHOW MODAL — renders inside BrowserRouter so navigate() works
   ====================================================================== */
function AutoLoginModal({ showModal, onClose }) {
  const location = useLocation();
  // Don't overlay on dedicated auth pages
  const authRoutes = ['/login', '/register', '/forgot-password', '/reset-password'];
  if (authRoutes.includes(location.pathname)) return null;
  if (!showModal) return null;
  return <LoginModal onClose={onClose} />;
}

/* ======================================================================
   MAIN APP
   ====================================================================== */
function AppInner() {
  const { isAuthenticated, loading } = useAuth();
  const [showModal, setShowModal] = useState(false);
  const [isDark, setIsDark] = useState(false);

  // ── Theme init ──────────────────────────────────────────────────────
  useEffect(() => {
    const savedTheme = localStorage.getItem('theme');
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    if (savedTheme === 'dark' || (!savedTheme && prefersDark)) {
      setIsDark(true);
      document.body.classList.add('dark');
    }
  }, []);

  const toggleTheme = useCallback(() => {
    setIsDark(prev => {
      const next = !prev;
      document.body.classList.toggle('dark', next);
      localStorage.setItem('theme', next ? 'dark' : 'light');
      return next;
    });
  }, []);

  // ── Auto-show modal once on first load for unauthenticated users ────
  useEffect(() => {
    if (loading) return;                     // wait for auth check to finish
    if (!isAuthenticated) {
      // Show modal only on the very first visit to "/"
      // (not on /register, /forgot-password, etc. — AutoLoginModal handles that filtering)
      setShowModal(true);
    }
  }, [loading, isAuthenticated]);

  // When user authenticates, hide modal
  useEffect(() => {
    if (isAuthenticated) setShowModal(false);
  }, [isAuthenticated]);

  const openModal  = useCallback(() => setShowModal(true),  []);
  const closeModal = useCallback(() => setShowModal(false), []);

  return (
    <div className="app-container">

      {/* ── Navbar ──────────────────────────────────────────────────── */}
      <nav className="navbar">
        <NavLink to="/" className="nav-brand">
          <SearchIcon size={22} />
          SearchEngineSim
        </NavLink>

        <div className="nav-links">
          <NavLink to="/" className={({ isActive }) => `nav-link${isActive ? ' nav-link-active' : ''}`} end>
            Search
          </NavLink>
          <NavLink to="/algorithms" className={({ isActive }) => `nav-link${isActive ? ' nav-link-active' : ''}`}>
            <GitBranch size={15} style={{ marginRight: 4, verticalAlign: 'middle' }} />
            Algorithms
          </NavLink>
          <NavLink to="/analytics" className={({ isActive }) => `nav-link${isActive ? ' nav-link-active' : ''}`}>
            Analytics
          </NavLink>
          <NavLink to="/documents" className={({ isActive }) => `nav-link${isActive ? ' nav-link-active' : ''}`}>
            Documents
          </NavLink>
          <NavLink to="/learn" className={({ isActive }) => `nav-link${isActive ? ' nav-link-active' : ''}`}>
            Learn DSA
          </NavLink>

          <NavAuthLinks onOpenModal={openModal} />

          <button className="theme-toggle" onClick={toggleTheme} aria-label="Toggle theme">
            {isDark ? <Sun size={20} /> : <Moon size={20} />}
          </button>
        </div>
      </nav>

      {/* ── Page content ─────────────────────────────────────────────── */}
      <main className="main-content">
        <ErrorBoundary>
          <Routes>
            {/* Phase 1-6 */}
            <Route path="/"             element={<SearchPage />} />
            <Route path="/algorithms"   element={<AlgorithmsPage />} />
            <Route path="/analytics"    element={<AnalyticsPage />} />
            <Route path="/documents"    element={<DocumentsPage />} />
            <Route path="/learn"        element={<LearningHubPage />} />
            <Route path="/learn/:slug"  element={<TopicDetailPage />} />

            {/* Auth pages (full-page fallback routes) */}
            <Route path="/login"           element={<LoginPage />} />
            <Route path="/register"        element={<RegisterPage />} />
            <Route path="/forgot-password" element={<ForgotPasswordPage />} />
            <Route path="/reset-password"  element={<ResetPasswordPage />} />
            <Route path="/learn/auth"      element={<AuthEducationPage />} />
          </Routes>
        </ErrorBoundary>
      </main>

      {/* ── Login Modal overlay ──────────────────────────────────────── */}
      <AutoLoginModal showModal={showModal} onClose={closeModal} />
    </div>
  );
}

/* ======================================================================
   ROOT — AuthProvider wraps BrowserRouter so useNavigate works inside modal
   ====================================================================== */
export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppInner />
      </AuthProvider>
    </BrowserRouter>
  );
}
