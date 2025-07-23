import apiClient from './index';

// --- User Management ---
export const getAllUsers = async () => apiClient.get('/api/admin/users').then(res => res.data);
export const deleteUser = async (userId) => apiClient.delete(`/api/admin/users/${userId}`);
export const promoteUser = async (userId) => apiClient.put(`/api/admin/users/${userId}/promote`).then(res => res.data);

// --- File Management ---
export const getAllFilesAdmin = async () => apiClient.get('/api/admin/files').then(res => res.data);

// --- Permission Management ---
export const grantPermission = async (permissionData) => apiClient.post('/api/admin/permissions', permissionData);

// THIS IS THE NEW FUNCTION FOR THE "All Permissions" TAB
export const getAllPermissions = async () => apiClient.get('/api/admin/permissions').then(res => res.data);

// THIS PATH IS UPDATED TO MATCH THE BACKEND FIX
export const getPermissionsForFile = async (fileId) => apiClient.get(`/api/admin/permissions/file/${fileId}`).then(res => res.data);

export const revokePermission = async (permissionId) => apiClient.delete(`/api/admin/permissions/${permissionId}`);

// --- Log/Alert Functions ---
export const getAllLogs = async () => apiClient.get('/api/logs').then(res => res.data);
export const getAllAlerts = async () => apiClient.get('/api/alerts').then(res => res.data);
