import React from 'react';
import { Box, CircularProgress } from '@mui/material';

/**
 * A simple component to display a centered loading spinner.
 * @returns {React.ReactNode} A Box component containing a CircularProgress indicator.
 */
const LoadingSpinner = () => (
  <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}>
    <CircularProgress />
  </Box>
);

export default LoadingSpinner;
