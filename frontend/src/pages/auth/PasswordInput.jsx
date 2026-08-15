import React, { useState } from 'react';
import { Eye, EyeOff } from 'lucide-react';

/**
 * PasswordInput — a styled password field with eye/eye-off toggle.
 *
 * Accepts all standard <input> props plus:
 *   id, value, onChange, autoComplete, required, placeholder
 *
 * The toggle button uses type="button" so it never submits a form.
 * The entered value is never logged.
 */
export default function PasswordInput({ id, name, value, onChange, autoComplete, required, placeholder }) {
  const [visible, setVisible] = useState(false);

  const toggle = (e) => {
    e.preventDefault(); // belt-and-suspenders: never submit
    setVisible(v => !v);
  };

  return (
    <div className="pw-field">
      <input
        id={id}
        name={name}
        type={visible ? 'text' : 'password'}
        value={value}
        onChange={onChange}
        autoComplete={autoComplete}
        required={required}
        placeholder={placeholder}
        className="pw-input"
      />
      <button
        type="button"
        className="pw-toggle"
        onClick={toggle}
        tabIndex={-1}            /* skip in tab order — keyboard users can still type password */
        aria-label={visible ? 'Hide password' : 'Show password'}
        title={visible ? 'Hide password' : 'Show password'}
      >
        {visible ? <EyeOff size={16} /> : <Eye size={16} />}
      </button>
    </div>
  );
}
