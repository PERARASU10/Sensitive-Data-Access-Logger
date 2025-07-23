import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import LoadingSpinner from '../common/LoadingSpinner';

/**
 * A component that guards routes, allowing access only to authenticated users.
 * If the user is not authenticated, they are redirected to the login page.
 * @param {Object} props - The component props.
 * @param {React.ReactNode} props.children - The child components to render if authenticated.
 * @returns {React.ReactNode} The child components or a redirect.
 */
const AuthGuard = ({ children }) => {
  const { user, loading } = useAuth();
  const location = useLocation();

  // Show a loading spinner while the auth state is being determined
  if (loading) {
    return <LoadingSpinner />;
  }

  // If not loading and there is no user, redirect to the login page
  if (!user) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // If the user is authenticated, render the protected content
  return children;
};

export default AuthGuard;
