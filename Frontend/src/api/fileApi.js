import apiClient from './index';

export const uploadFile = async (formData) => {
  const response = await apiClient.post('/api/files/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return response.data;
};

export const downloadFile = async (fileId) => {
  return await apiClient.get(`/api/files/download/${fileId}`, { responseType: 'blob' });
};

// New function to view a file
export const viewFile = async (fileId) => {
  return await apiClient.get(`/api/files/view/${fileId}`, { responseType: 'blob' });
};

export const getFiles = async () => {
  try {
    const response = await apiClient.get('/api/files');
    return response.data;
  } catch (error) {
    console.error("Failed to fetch files:", error);
    return [];
  }
};
