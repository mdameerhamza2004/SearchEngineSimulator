import React, { useState } from 'react';
import { useSearchParams, useNavigate, Link } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';
import AuthErrorModal from './AuthErrorModal';
import PasswordInput from './PasswordInput';
import { getFriendlyError, CONNECTION_ERROR } from './errorUtils';
import './Auth.css';

const ResetPasswordPage = () => {
  const [searchParams]                        = useSearchParams();
  const token                                 = searchParams.get('token');
  const [password, setPassword]               = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [success, setSuccess]                 = useState(false);
  const [loading, setLoading]                 = useState(false);
  const [errorModal, setErrorModal]           = useState(null);
  const navigate = useNavigate();

  const showError = (title, err) => {
    const friendly = getFriendlyError(err);
    setErrorModal({ title, message: friendly ?? CONNECTION_ERROR });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (password !== confirmPassword) {
      return setErrorModal({
        title: 'Passwords Do Not Match',
        message: 'The new password and the confirmation password do not match. Please try again.',
      });
    }
    if (!token) {
      return setErrorModal({
        title: 'Invalid Reset Link',
        message: 'No reset token was found in the URL. Please request a new password reset link.',
      });
    }
    setLoading(true);
    try {
      await apiClient.resetPassword({ token, newPassword: password });
      setSuccess(true);
      setTimeout(() => navigate('/login'), 3500);
    } catch (err) {
      showError('Password Reset Failed', err);
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
          <h2>Reset Password</h2>

          {success ? (
            <>
              <div className="success">
                Your password has been successfully reset. Redirecting to login in a moment…
              </div>
              <div className="auth-links">
                <Link to="/login">Go to Login now</Link>
              </div>
            </>
          ) : (
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label htmlFor="rp-password">New Password</label>
                <PasswordInput
                  id="rp-password"
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  autoComplete="new-password"
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="rp-confirm">Confirm New Password</label>
                <PasswordInput
                  id="rp-confirm"
                  value={confirmPassword}
                  onChange={e => setConfirmPassword(e.target.value)}
                  autoComplete="new-password"
                  required
                />
              </div>
              <button type="submit" className="btn primary" disabled={loading}>
                {loading ? 'Resetting…' : 'Reset Password'}
              </button>
            </form>
          )}
        </div>
      </div>
    </>
  );
};

export default ResetPasswordPage;
