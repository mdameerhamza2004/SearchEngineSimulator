import React, { useEffect, useCallback } from 'react';
import { X, AlertCircle } from 'lucide-react';

/**
 * AuthErrorModal — reusable error popup for authentication pages.
 *
 * Props:
 *   title   {string}   Dialog heading, e.g. "Registration Failed"
 *   message {string}   Body message shown to the user
 *   onClose {function} Called when user dismisses the dialog
 */
export default function AuthErrorModal({ title, message, onClose }) {
  // Close on Escape
  useEffect(() => {
    const handler = (e) => { if (e.key === 'Escape') onClose(); };
    document.addEventListener('keydown', handler);
    return () => document.removeEventListener('keydown', handler);
  }, [onClose]);

  // Close on backdrop click
  const handleBackdrop = useCallback((e) => {
    if (e.target === e.currentTarget) onClose();
  }, [onClose]);

  return (
    <div className="aem-backdrop" onClick={handleBackdrop} role="alertdialog" aria-modal="true" aria-labelledby="aem-title">
      <div className="aem-panel">

        {/* X close */}
        <button className="aem-close" onClick={onClose} aria-label="Close error dialog">
          <X size={16} />
        </button>

        {/* Icon + title */}
        <div className="aem-icon-wrap">
          <AlertCircle size={36} className="aem-icon" />
        </div>

        <h2 className="aem-title" id="aem-title">{title}</h2>
        <p className="aem-message">{message}</p>

        <button className="aem-btn-close" onClick={onClose}>
          Close
        </button>
      </div>
    </div>
  );
}
