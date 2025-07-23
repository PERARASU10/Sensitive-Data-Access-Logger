import axios from 'axios';

const apiClient = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL,
});

// Request interceptor to add the auth token header to requests
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle global errors
apiClient.interceptors.response.use(
  (response) => response, // Simply return the response if it's successful
  (error) => {
    // If the error is a 401 or 403, it means the user is not authorized.
    // We can redirect to login or handle it globally.
    if (error.response && [401, 403].includes(error.response.status)) {
      // This check prevents a redirect loop from the login page itself
      if (window.location.pathname !== '/login') {
        localStorage.removeItem('token'); // Clear invalid token
        window.location = '/login'; // Force a reload to the login page
      }
    }
    return Promise.reject(error);
  }
);

export default apiClient;
