import { createContext, useContext, useState, useEffect } from 'react';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('user');
    return stored ? JSON.parse(stored) : null;
  });
  const [token, setToken] = useState(() => localStorage.getItem('token'));

  // --- LOGIN ---
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

  // --- REGISTER ---
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

  // --- LOGOUT ---
  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('user');
    localStorage.removeItem('token');
  };

  // --- UPDATE PROFILE ---
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

  return (
    <AuthContext.Provider
      value={{ user, token, login, register, logout, updateProfile }}
    >
      {children}
    </AuthContext.Provider>
  );
}

/**
 * Hook pratique pour accéder au contexte d'authentification.
 * Doit être utilisé à l'intérieur d'un <AuthProvider>.
 */
export function useAuth() {
  const context = useContext(AuthContext);
  if (context === null) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
