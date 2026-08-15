import React from 'react';
import './AuthEducation.css';

const AuthEducationPage = () => {
  return (
    <div className="education-container">
      <header className="education-header">
        <h1>Authentication Deep Dive</h1>
        <p>Understanding the security mechanisms behind modern web applications.</p>
      </header>

      <section className="education-section">
        <h2>Authentication vs Authorization</h2>
        <div className="comparison-grid">
          <div className="comparison-card">
            <h3>Authentication (AuthN)</h3>
            <p>Verifying <strong>who</strong> the user is.</p>
            <ul>
              <li>Logging in with username/password</li>
              <li>Multi-factor authentication (MFA)</li>
              <li>Biometrics (FaceID, Fingerprint)</li>
            </ul>
          </div>
          <div className="comparison-card">
            <h3>Authorization (AuthZ)</h3>
            <p>Verifying <strong>what</strong> the user can do.</p>
            <ul>
              <li>Role-based access control (RBAC)</li>
              <li>Checking if a user can edit an article</li>
              <li>Admin privileges vs regular user</li>
            </ul>
          </div>
        </div>
      </section>

      <section className="education-section">
        <h2>Sessions vs JWTs</h2>
        <p>How does the server remember you after you log in?</p>
        <div className="comparison-grid">
          <div className="comparison-card">
            <h3>Session-based Authentication</h3>
            <p>Server creates a session, stores it in DB/Memory, and sends a Session ID to client.</p>
            <p><strong>Pros:</strong> Easy to revoke, smaller payload.</p>
            <p><strong>Cons:</strong> Harder to scale (stateful), requires DB lookup per request.</p>
          </div>
          <div className="comparison-card">
            <h3>JSON Web Tokens (JWT)</h3>
            <p>Server cryptographically signs user data into a token and sends it to client. Server stores nothing.</p>
            <p><strong>Pros:</strong> Scalable (stateless), server doesn't need to query DB for auth.</p>
            <p><strong>Cons:</strong> Hard to revoke before expiry, larger payload.</p>
          </div>
        </div>
      </section>

      <section className="education-section">
        <h2>Why HttpOnly Cookies &gt; localStorage</h2>
        <div className="threat-model">
          <h4>Cross-Site Scripting (XSS)</h4>
          <p>If an attacker injects malicious JavaScript into your site, they can read <code>localStorage</code>. If your JWT is there, they steal it and impersonate the user.</p>
          <h4>The Solution: HttpOnly Cookies</h4>
          <p>An <code>HttpOnly</code> cookie cannot be accessed via JavaScript (<code>document.cookie</code>). It is automatically sent by the browser with every request to the backend. This neutralizes XSS attacks targeting auth tokens.</p>
          <h4>What about CSRF?</h4>
          <p>Cookies are vulnerable to Cross-Site Request Forgery (CSRF). To mitigate this, modern cookies use the <code>SameSite=Lax</code> or <code>SameSite=Strict</code> attribute, preventing the browser from sending the cookie in cross-origin requests initiated by third-party sites.</p>
        </div>
      </section>

      <section className="education-section">
        <h2>Password Security: Hashing vs Encryption</h2>
        <p>Passwords should NEVER be encrypted. They must be <strong>hashed</strong>.</p>
        <ul>
          <li><strong>Encryption:</strong> Two-way function. You can decrypt it back to plain text with a key. If the key leaks, all passwords are compromised.</li>
          <li><strong>Hashing:</strong> One-way function. You cannot reverse a hash back to plain text.</li>
        </ul>
        <div className="highlight-box">
          <h4>BCrypt &amp; Salting</h4>
          <p>A fast hash like MD5 or SHA-256 is bad for passwords because attackers can brute-force them quickly using GPUs. <strong>BCrypt</strong> is intentionally slow, making brute-force attacks computationally expensive.</p>
          <p><strong>Salting</strong> adds a random string to the password before hashing. This defeats "Rainbow Tables" (precomputed hash lists), ensuring that two users with the password "password123" have completely different hashes in the database.</p>
        </div>
      </section>

      <section className="education-section">
        <h2>Google OAuth 2.0 / OpenID Connect</h2>
        <p>When you click "Sign in with Google", here is what happens:</p>
        <ol>
          <li>Your app redirects the user to Google's authorization server.</li>
          <li>User logs into Google and grants permission to your app.</li>
          <li>Google redirects back to your app with an Authorization Code.</li>
          <li>Your backend securely exchanges this code for an Access Token and ID Token.</li>
          <li>Your backend verifies the ID Token, extracts the user's email, creates a session, and sets an HttpOnly cookie.</li>
        </ol>
      </section>
    </div>
  );
};

export default AuthEducationPage;
