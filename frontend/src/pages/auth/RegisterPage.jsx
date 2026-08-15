import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import AuthErrorModal from './AuthErrorModal';
import PasswordInput from './PasswordInput';
import { getFriendlyError, CONNECTION_ERROR } from './errorUtils';
import './Auth.css';

const RegisterPage = () => {
  const [formData, setFormData] = useState({
    name: '', email: '', password: '', confirmPassword: '',
  });
  const [errorModal, setErrorModal] = useState(null); // { title, message }
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate     = useNavigate();

  const handleChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });

  const showError = (title, err) => {
    const friendly = getFriendlyError(err);
    setErrorModal({ title, message: friendly ?? CONNECTION_ERROR });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (formData.password !== formData.confirmPassword) {
      return setErrorModal({
        title: 'Passwords Do Not Match',
        message: 'The password and confirmation password you entered do not match. Please re-enter them.',
      });
    }
    setLoading(true);
    try {
      await register({ name: formData.name, email: formData.email, password: formData.password });
      navigate('/login');
    } catch (err) {
      showError('Registration Failed', err);
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
          <h2>Create Account</h2>

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="reg-name">Name</label>
              <input
                id="reg-name"
                type="text"
                name="name"
                value={formData.name}
                onChange={handleChange}
                required
                autoComplete="name"
              />
            </div>
            <div className="form-group">
              <label htmlFor="reg-email">Email</label>
              <input
                id="reg-email"
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                required
                autoComplete="email"
              />
            </div>
            <div className="form-group">
              <label htmlFor="reg-password">Password</label>
              <PasswordInput
                id="reg-password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                autoComplete="new-password"
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="reg-confirm">Confirm Password</label>
              <PasswordInput
                id="reg-confirm"
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleChange}
                autoComplete="new-password"
                required
              />
            </div>
            <button type="submit" className="btn primary" disabled={loading}>
              {loading ? 'Creating account…' : 'Sign Up'}
            </button>
          </form>

          <div className="auth-links">
            <Link to="/login">Already have an account? Login</Link>
          </div>
        </div>
      </div>
    </>
  );
};

export default RegisterPage;
