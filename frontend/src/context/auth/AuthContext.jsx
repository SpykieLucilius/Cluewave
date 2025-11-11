import { createContext, useContext, useState, useEffect } from 'react';

/**
 * Authentication context manages user state, JWT tokens and exposes
 * operations for logging in, registering and logging out.  The token and
 * user information are persisted to localStorage so that the session
 * survives page refreshes.  Components can consume this context via
 * {@link useAuth}.
 */
const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('user');
    return stored ? JSON.parse(stored) : null;
  });
  const [token, setToken] = useState(() => localStorage.getItem('token'));

  /**
   * Perform a login against the backend.  On success update local state and
   * persist the token and user to localStorage.  Throws an error on
   * authentication failure.
   */
  const login = async (email, password) => {
    const res = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });
    if (!res.ok) {
      const msg = await res.text();
      throw new Error(msg || 'Unable to login');
    }
    const data = await res.json();
    setUser(data.user);
    setToken(data.accessToken);
    localStorage.setItem('user', JSON.stringify(data.user));
    localStorage.setItem('token', data.accessToken);
  };

  /**
   * Register a new user with the backend.  On success log the user in.
   */
  const register = async (username, email, password) => {
    const res = await fetch('/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, email, password })
    });
    if (!res.ok) {
      const msg = await res.text();
      throw new Error(msg || 'Unable to register');
    }
    const data = await res.json();
    setUser(data.user);
    setToken(data.accessToken);
    localStorage.setItem('user', JSON.stringify(data.user));
    localStorage.setItem('token', data.accessToken);
  };

  /**
   * Clear the current session.  Removes all authentication state from memory
   * and localStorage.
   */
  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('user');
    localStorage.removeItem('token');
  };

  return (
    <AuthContext.Provider value={{ user, token, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

/**
 * Convenience hook for consuming the AuthContext.  Throws an error if used
 * outside of an AuthProvider.
 */
export function useAuth() {
  const context = useContext(AuthContext);
  if (context === null) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
