import React, { createContext, useState, useEffect, useContext } from 'react';
import { apiClient } from '../api/apiClient';

const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        // apiClient.request() returns parsed JSON directly (no .data wrapper)
        const response = await apiClient.me();
        if (response && (response.id || response.email)) {
          setUser(response);
          setIsAuthenticated(true);
        }
      } catch (error) {
        // Expected when user is not logged in (401 / network) — silently clear state
        // Use debug instead of error to avoid alarming browser console
        console.debug('[AuthContext] /auth/me not authenticated:', error.message);
        setUser(null);
        setIsAuthenticated(false);
      } finally {
        setLoading(false);
      }
    };
    fetchUser();
  }, []);

  const login = async (email, password) => {
    // apiClient.login() takes a single object {email, password}
    const response = await apiClient.login({ email, password });
    // Response is the user object directly
    const userData = response?.user || response;
    setUser(userData);
    setIsAuthenticated(true);
    return response;
  };

  const register = async (userData) => {
    const response = await apiClient.register(userData);
    return response;
  };

  const logout = async () => {
    await apiClient.logout();
    setUser(null);
    setIsAuthenticated(false);
  };

  return (
    <AuthContext.Provider value={{ user, isAuthenticated, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};
