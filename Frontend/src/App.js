import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import AdminPage from './pages/AdminPage';
import AuthGuard from './components/Auth/AuthGuard';
import RoleGuard from './components/Auth/RoleGuard';
import Navbar from './components/layout/Navbar';
import { useAuth } from './hooks/useAuth';

function App() {
  const { user } = useAuth();

  return (
    <div className="App">
      <Navbar />
      <Routes>
        {/* Public Routes */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* Protected User Route */}
        <Route 
          path="/dashboard" 
          element={
            <AuthGuard>
              <DashboardPage />
            </AuthGuard>
          }
        />

        {/* Protected Admin Route */}
        <Route 
          path="/admin" 
          element={
            <AuthGuard>
              <RoleGuard role="ADMIN">
                <AdminPage />
              </RoleGuard>
            </AuthGuard>
          }
        />

        {/* Redirect Logic */}
        <Route 
          path="*" 
          element={<Navigate to={user ? (user.role === 'ADMIN' ? '/admin' : '/dashboard') : '/login'} />} 
        />
      </Routes>
    </div>
  );
}

export default App;
