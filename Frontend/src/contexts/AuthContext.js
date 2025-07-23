import React, { createContext, useState, useEffect } from 'react';
import { loginUser, registerUser } from '../api/authApi';
import { jwtDecode } from 'jwt-decode';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const decodedToken = jwtDecode(token);
        // Check if the token is expired
        if (decodedToken.exp * 1000 > Date.now()) {
            // **THE FIX IS HERE: Read the role from the 'roles' claim in the token**
            setUser({ 
                username: decodedToken.sub, 
                role: decodedToken.roles[0].replace('ROLE_', '') // Get role from token
            });
        } else {
            localStorage.removeItem('token');
        }
      } catch (error) {
        console.error('Invalid token found in storage:', error);
        localStorage.removeItem('token');
      }
    }
    setLoading(false);
  }, []);

  const login = async (credentials) => {
    const authResponse = await loginUser(credentials);
    localStorage.setItem('token', authResponse.token);
    setUser({ 
        username: authResponse.username, 
        role: authResponse.role 
    });
    return authResponse;
  };

  const register = async (userData) => {
    const authResponse = await registerUser(userData);
    localStorage.setItem('token', authResponse.token);
    setUser({ 
        username: authResponse.username, 
        role: authResponse.role 
    });
    return authResponse;
  };

  const logout = () => {
    localStorage.removeItem('token');
    setUser(null);
  };

  const value = {
    user,
    loading,
    login,
    register,
    logout,
  };

  return (
    <AuthContext.Provider value={value}>
      {!loading && children}
    </AuthContext.Provider>
  );
};
