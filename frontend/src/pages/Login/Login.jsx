import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/auth/AuthContext.jsx';
import '../../styles/Login.css';

export default function Login() {
  const navigate = useNavigate();
  const { login, socialLogin } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);

  const handleGoogleCredentialResponse = async (response) => {
    const idToken = response.credential;
    setError(null);
    try {
      await socialLogin('google', idToken);
      navigate('/');
    } catch (err) {
      setError(err.message);
    }
  };
  useEffect(() => {
    const script = document.createElement('script');
    script.src = 'https://accounts.google.com/gsi/client';
    script.async = true;
    script.defer = true;
    document.body.appendChild(script);
    script.onload = () => {
      if (window.google && window.google.accounts) {
        window.google.accounts.id.initialize({
          client_id: import.meta.env.VITE_GOOGLE_CLIENT_ID,
          callback: handleGoogleCredentialResponse,
        });
        const btn = document.getElementById('google-signin-button');
        if (btn) {
          window.google.accounts.id.renderButton(btn, {
            theme: 'outline',
            size: 'large',
            width: '100%',
          });
        }
      }
    };
    return () => {
      document.body.removeChild(script);
    };
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      await login(email, password);
      navigate('/');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <main className="login-page">
      <div className="login-container">
        <h1 className="login-title">Player Login</h1>
        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          {error && <p className="error-message">{error}</p>}
          <button type="submit" className="login-button">Login</button>
          <div id="google-signin-button" style={{ marginTop: '1rem' }}></div>
          <p className="register-link">
            Don't have an account? <Link to="/register">Register</Link>
          </p>
        </form>
      </div>
    </main>
  );
}
