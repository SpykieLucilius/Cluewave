import { createContext, useContext, useState } from 'react';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('user');
    return stored ? JSON.parse(stored) : null;
  });
  const [token, setToken] = useState(() => localStorage.getItem('token'));

  const login = async (email, password) => {
    const res = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
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

  const register = async (username, email, password) => {
    const res = await fetch('/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, email, password }),
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

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('user');
    localStorage.removeItem('token');
  };

  /**
   * Met à jour le profil de l’utilisateur connecté.
   * currentPassword est obligatoire ; les autres champs peuvent être omis pour rester inchangés.
   */
  const updateProfile = async (updates) => {
    if (!token) throw new Error('Not authenticated');
    const res = await fetch('/api/auth/profile', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(updates),
    });
    if (!res.ok) {
      const msg = await res.text();
      throw new Error(msg || 'Unable to update profile');
    }
    const data = await res.json();
    setUser(data);
    localStorage.setItem('user', JSON.stringify(data));
    return data;
  };

  /**
   * Performs a social login using an identity provider such as Google.  The
   * provider name and ID token are sent to the backend.  On success the
   * user and token are stored in state and localStorage.  Errors from the
   * backend are surfaced as thrown exceptions.
   *
   * @param provider the name of the social provider (e.g. "google")
   * @param idToken  the ID token returned by the provider
   */
  const socialLogin = async (provider, idToken) => {
    const res = await fetch('/api/auth/social-login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ provider, idToken }),
    });
    if (!res.ok) {
      const msg = await res.text();
      throw new Error(msg || 'Unable to login via social provider');
    }
    const data = await res.json();
    setUser(data.user);
    setToken(data.accessToken);
    localStorage.setItem('user', JSON.stringify(data.user));
    localStorage.setItem('token', data.accessToken);
  };

  return (
    <AuthContext.Provider
      value={{ user, token, login, register, logout, updateProfile, socialLogin }}
    >
      {children}
    </AuthContext.Provider>
  );
}

/**
 * Hook pratique pour accéder au contexte d’authentification.
 */
export function useAuth() {
  const context = useContext(AuthContext);
  if (context === null) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
