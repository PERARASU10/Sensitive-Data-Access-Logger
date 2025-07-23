import React from 'react';
import { Alert } from '@mui/material';

/**
 * A reusable component to display an error message in an Alert box.
 * @param {Object} props - The component props.
 * @param {string} props.message - The error message to display. If empty, nothing is rendered.
 * @returns {React.ReactNode|null} An Alert component with the error message, or null.
 */
const ErrorMessage = ({ message }) => {
  if (!message) {
    return null;
  }

  return (
    <Alert severity="error" sx={{ mt: 2, width: '100%' }}>
      {message}
    </Alert>
  );
};

export default ErrorMessage;
