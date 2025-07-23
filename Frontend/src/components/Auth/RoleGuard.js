import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

/**
 * A component that guards routes, allowing access only to users with a specific role.
 * If the user does not have the required role, they are redirected.
 * @param {Object} props - The component props.
 * @param {React.ReactNode} props.children - The child components to render if the role matches.
 * @param {string} props.role - The required role (e.g., 'ADMIN').
 * @returns {React.ReactNode} The child components or a redirect.
 */
const RoleGuard = ({ children, role }) => {
  const { user } = useAuth();

  // This guard assumes AuthGuard has already run, so a user object should exist.
  // If the user's role does not match the required role, redirect them.
  if (!user || user.role !== role) {
    // Redirect non-admins to the standard user dashboard.
    return <Navigate to="/dashboard" replace />;
  }

  // If the user has the correct role, render the protected content.
  return children;
};

export default RoleGuard;
