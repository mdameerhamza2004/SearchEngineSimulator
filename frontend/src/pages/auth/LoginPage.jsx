import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import AuthErrorModal from './AuthErrorModal';
import PasswordInput from './PasswordInput';
import { getFriendlyError, CONNECTION_ERROR } from './errorUtils';
import './Auth.css';

const LoginPage = () => {
  const [email, setEmail]       = useState('');
  const [password, setPassword] = useState('');
  const [errorModal, setErrorModal] = useState(null); // { title, message }
  const [loading, setLoading]   = useState(false);
  const { login }  = useAuth();
  const navigate   = useNavigate();

  const showError = (title, err) => {
    const friendly = getFriendlyError(err);
    setErrorModal({ title, message: friendly ?? CONNECTION_ERROR });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await login(email, password);
      navigate('/');
    } catch (err) {
      showError('Login Failed', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      {errorModal && (
        <AuthErrorModal
          title={errorModal.title}
          message={errorModal.message}
          onClose={() => setErrorModal(null)}
        />
      )}

      <div className="auth-container">
        <div className="auth-card">
          <h2>Login</h2>

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="login-email">Email</label>
              <input
                id="login-email"
                type="email"
                value={email}
                onChange={e => setEmail(e.target.value)}
                required
                autoComplete="email"
              />
            </div>
            <div className="form-group">
              <label htmlFor="login-password">Password</label>
              <PasswordInput
                id="login-password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                autoComplete="current-password"
                required
              />
            </div>
            <button type="submit" className="btn primary" disabled={loading}>
              {loading ? 'Signing in…' : 'Sign In'}
            </button>
          </form>

          <div className="divider">OR</div>

          <a href="http://localhost:8080/oauth2/authorization/google" className="btn google">
            Sign in with Google
          </a>

          <div className="auth-links">
            <Link to="/forgot-password">Forgot Password?</Link>
            <Link to="/register">Create an Account</Link>
          </div>
        </div>
      </div>
    </>
  );
};

export default LoginPage;
