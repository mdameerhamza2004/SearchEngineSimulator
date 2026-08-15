import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';
import AuthErrorModal from './AuthErrorModal';
import { getFriendlyError, CONNECTION_ERROR } from './errorUtils';
import './Auth.css';

const ForgotPasswordPage = () => {
  const [email, setEmail]       = useState('');
  const [success, setSuccess]   = useState(false);
  const [loading, setLoading]   = useState(false);
  const [errorModal, setErrorModal] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await apiClient.forgotPassword({ email });
    } catch (err) {
      // Intentionally swallowed — we always show the generic success message
      // to prevent user-enumeration attacks (attacker can't tell if email exists).
      // Only show error popup for genuine unexpected failures.
      const friendly = getFriendlyError(err);
      if (friendly === null) {
        // Network error: backend is down — show popup
        setErrorModal({
          title: 'Cannot Send Reset Email',
          message: CONNECTION_ERROR,
        });
        setLoading(false);
        return;
      }
    } finally {
      setLoading(false);
    }
    setSuccess(true);
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
          <h2>Forgot Password</h2>

          {success && (
            <div className="success">
              If that email address is registered, a password reset link has been sent. Please check your inbox.
            </div>
          )}

          {!success && (
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label htmlFor="fp-email">Email</label>
                <input
                  id="fp-email"
                  type="email"
                  value={email}
                  onChange={e => setEmail(e.target.value)}
                  required
                  autoComplete="email"
                />
              </div>
              <button type="submit" className="btn primary" disabled={loading}>
                {loading ? 'Sending…' : 'Send Reset Link'}
              </button>
            </form>
          )}

          <div className="auth-links">
            <Link to="/login">Back to Login</Link>
          </div>
        </div>
      </div>
    </>
  );
};

export default ForgotPasswordPage;
