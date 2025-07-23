import React, { useState, useEffect, useCallback } from 'react';
import {
    Container, Typography, Box, Paper, Tabs, Tab, Button, TextField, Chip,
    Dialog, DialogActions, DialogContent, DialogTitle, Select, MenuItem, FormControl, InputLabel
} from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import {
    getAllUsers, deleteUser, promoteUser, getAllFilesAdmin, grantPermission,
    getAllLogs, getAllAlerts, getPermissionsForFile, revokePermission, getAllPermissions
} from '../api/adminApi';
import { uploadFile } from '../api/fileApi';
import ErrorMessage from '../components/common/ErrorMessage';
import LoadingSpinner from '../components/common/LoadingSpinner';

const AdminPage = () => {
    // State for managing the current active tab
    const [tabIndex, setTabIndex] = useState(0);

    // General loading and error states
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    // State for each data grid
    const [users, setUsers] = useState([]);
    const [files, setFiles] = useState([]);
    const [logs, setLogs] = useState([]);
    const [alerts, setAlerts] = useState([]);
    const [allPermissions, setAllPermissions] = useState([]); // New state for the "All Permissions" tab

    // State for the file upload functionality
    const [selectedFile, setSelectedFile] = useState(null);
    const [uploadStatus, setUploadStatus] = useState('');

    // State for the permission management dialog
    const [permissionDialogOpen, setPermissionDialogOpen] = useState(false);
    const [selectedFileForPermission, setSelectedFileForPermission] = useState(null);
    const [currentPermissions, setCurrentPermissions] = useState([]);
    const [selectedUserForPermission, setSelectedUserForPermission] = useState('');
    const [permissionType, setPermissionType] = useState('VIEW');

    // Fetches data based on the currently active tab
    const fetchData = useCallback(async () => {
        setLoading(true);
        setError('');
        try {
            const dataFetchers = [getAllUsers, getAllFilesAdmin, getAllPermissions, getAllLogs, getAllAlerts];
            if (tabIndex < dataFetchers.length) {
                const data = await dataFetchers[tabIndex]();
                const setters = [setUsers, setFiles, setAllPermissions, setLogs, setAlerts];
                // Ensure every item has a unique id for the DataGrid to work correctly
                setters[tabIndex](data.map(item => ({ ...item, id: item.id || Math.random() })));
            }
        } catch (err) {
            setError('Failed to fetch data.');
            console.error(err);
        } finally {
            setLoading(false);
        }
    }, [tabIndex]);

    // Effect to re-fetch data whenever the active tab changes
    useEffect(() => {
        if (tabIndex < 5) { // Don't fetch for "Upload File" tab
            fetchData();
        } else {
            setLoading(false);
        }
    }, [tabIndex, fetchData]);

    const handleTabChange = (event, newValue) => setTabIndex(newValue);
    const handleFileChange = (event) => {
        setSelectedFile(event.target.files[0]);
        setUploadStatus('');
    };

    // A generic handler for actions like delete, promote, revoke, and grant
    const handleAction = async (action, ...args) => {
        const actionName = action.name.replace(/([A-Z])/g, ' $1').toLowerCase();
        // Show a confirmation dialog for destructive actions
        if (actionName.includes('delete') || actionName.includes('promote') || actionName.includes('revoke')) {
            if (!window.confirm(`Are you sure you want to ${actionName}?`)) return;
        }
        try {
            await action(...args);
            // After a permission change in the dialog, refresh the dialog's list
            if (action === grantPermission || action === revokePermission) {
                const updatedPerms = await getPermissionsForFile(selectedFileForPermission.id);
                setCurrentPermissions(updatedPerms);
            } else {
                // For all other actions, refresh the main grid data
                fetchData();
            }
        } catch (err) {
            setError(`Failed to ${actionName}.`);
            console.error(err);
        }
    };

    // Opens the permission dialog and fetches the current permissions for the selected file
    const handleOpenPermissionDialog = async (file) => {
        setSelectedFileForPermission(file);
        try {
            const permissions = await getPermissionsForFile(file.id);
            setCurrentPermissions(permissions);
            setPermissionDialogOpen(true);
        } catch (err) {
            setError('Could not fetch current permissions.');
        }
    };

    // Resets state when the permission dialog is closed
    const handleClosePermissionDialog = () => {
        setPermissionDialogOpen(false);
        setSelectedFileForPermission(null);
        setCurrentPermissions([]);
        setSelectedUserForPermission('');
        setPermissionType('VIEW');
    };

    // Handles the file upload process
    const handleFileUpload = async () => {
        if (!selectedFile) return;
        const formData = new FormData();
        formData.append('file', selectedFile);
        setUploadStatus('Uploading...');
        try {
            await uploadFile(formData);
            setUploadStatus('File uploaded successfully!');
            setSelectedFile(null);
            document.querySelector('input[type="file"]').value = '';
            if (tabIndex === 1) fetchData(); // Refresh file list if on the File Management tab
        } catch (err) {
            setUploadStatus(`Upload failed. The file may be too large or an error occurred.`);
        }
    };

    // Column definitions for all the DataGrids
    const columns = {
        users: [
            { field: 'id', headerName: 'ID', width: 70 },
            { field: 'username', headerName: 'Username', flex: 1 },
            { field: 'role', headerName: 'Role', width: 120 },
            { field: 'actions', headerName: 'Actions', width: 300, sortable: false, renderCell: ({ row }) => (
                <Box sx={{ display: 'flex', gap: 1 }}>
                    <Button variant="outlined" size="small" onClick={() => handleAction(promoteUser, row.id)} disabled={row.role === 'ADMIN'}>Promote</Button>
                    <Button variant="outlined" size="small" color="error" onClick={() => handleAction(deleteUser, row.id)}>Delete</Button>
                </Box>
            )}
        ],
        files: [
            { field: 'id', headerName: 'ID', width: 70 },
            { field: 'name', headerName: 'File Name', flex: 1 },
            { field: 'uploadTime', headerName: 'Upload Time', width: 200, valueGetter: ({ value }) => new Date(value).toLocaleString() },
            { field: 'actions', headerName: 'Actions', width: 200, sortable: false, renderCell: ({ row }) => <Button variant="outlined" size="small" onClick={() => handleOpenPermissionDialog(row)}>Manage Permissions</Button> }
        ],
        permissions: [ // Columns for the new "All Permissions" tab
            { field: 'id', headerName: 'Perm ID', width: 90 },
            { field: 'username', headerName: 'User', flex: 1 },
            { field: 'fileName', headerName: 'File', flex: 1 },
            { field: 'permissionType', headerName: 'Permission', width: 150 },
            { field: 'actions', headerName: 'Actions', width: 120, sortable: false, renderCell: ({ row }) => <Button variant="outlined" size="small" color="error" onClick={() => handleAction(revokePermission, row.id)}>Revoke</Button> }
        ],
        logs: [
            { field: 'id', headerName: 'ID', width: 70 },
            { field: 'username', headerName: 'User', width: 150 },
            { field: 'fileName', headerName: 'File', flex: 1 },
            { field: 'actionType', headerName: 'Action', width: 120 },
            { field: 'status', headerName: 'Status', width: 120, renderCell: ({ value }) => <Chip label={value} color={value === 'APPROVED' ? 'success' : 'error'} size="small" /> },
            { field: 'ipAddress', headerName: 'IP Address', width: 150 },
            { field: 'timestamp', headerName: 'Timestamp', width: 200, valueGetter: ({ value }) => new Date(value).toLocaleString() },
        ],
        alerts: [
            { field: 'id', headerName: 'ID', width: 70 },
            { field: 'username', headerName: 'User', width: 150 },
            { field: 'message', headerName: 'Message', flex: 1 },
            { field: 'createdAt', headerName: 'Timestamp', width: 200, valueGetter: ({ value }) => new Date(value).toLocaleString() },
        ],
    };

    // Helper function to render a DataGrid or a loading spinner
    const renderGrid = (data, gridColumns) => (loading ? <LoadingSpinner /> : <DataGrid rows={data} columns={gridColumns} autoHeight pageSizeOptions={[10, 25]} />);

    return (
        <Container maxWidth="xl" sx={{ mt: 4, mb: 4 }}>
            <Paper sx={{ p: 3 }}>
                <Typography variant="h4" gutterBottom>Admin Dashboard</Typography>
                <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
                    <Tabs value={tabIndex} onChange={handleTabChange} variant="scrollable" scrollButtons="auto">
                        {["User Management", "File Management", "All Permissions", "Access Logs", "Security Alerts", "Upload File"].map(label => <Tab key={label} label={label} />)}
                    </Tabs>
                </Box>
                <ErrorMessage message={error} />
                <Box sx={{ pt: 2, minHeight: '60vh' }}>
                    {tabIndex === 0 && renderGrid(users, columns.users)}
                    {tabIndex === 1 && renderGrid(files, columns.files)}
                    {tabIndex === 2 && renderGrid(allPermissions, columns.permissions)}
                    {tabIndex === 3 && renderGrid(logs, columns.logs)}
                    {tabIndex === 4 && renderGrid(alerts, columns.alerts)}
                    {tabIndex === 5 && (
                        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, alignItems: 'flex-start', mt: 2 }}>
                            <Typography variant="h6">Upload New Document</Typography>
                            <TextField type="file" onChange={handleFileChange} />
                            <Button variant="contained" onClick={handleFileUpload} disabled={!selectedFile}>Upload</Button>
                            {uploadStatus && <Typography color={uploadStatus.includes('failed') ? 'error' : 'success.main'}>{uploadStatus}</Typography>}
                        </Box>
                    )}
                </Box>
            </Paper>

            {/* Permission Granting and Management Dialog */}
            <Dialog open={permissionDialogOpen} onClose={handleClosePermissionDialog} fullWidth>
                <DialogTitle>Manage Permissions for: {selectedFileForPermission?.name}</DialogTitle>
                <DialogContent>
                    <Typography variant="h6" gutterBottom>Current Permissions</Typography>
                    {currentPermissions.length > 0 ? (
                        currentPermissions.map(p => (
                            <Box key={p.id} sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1, p: 1, border: '1px solid #ddd', borderRadius: 1 }}>
                                <Typography>{p.username} - {p.permissionType}</Typography>
                                <Button size="small" color="error" onClick={() => handleAction(revokePermission, p.id)}>Revoke</Button>
                            </Box>
                        ))
                    ) : <Typography>No permissions granted for this file.</Typography>}
                    
                    <Typography variant="h6" sx={{ mt: 3 }} gutterBottom>Grant New Permission</Typography>
                    <FormControl fullWidth margin="normal">
                        <InputLabel>User</InputLabel>
                        <Select value={selectedUserForPermission} label="User" onChange={(e) => setSelectedUserForPermission(e.target.value)}>
                            {users.map(user => <MenuItem key={user.id} value={user.username}>{user.username}</MenuItem>)}
                        </Select>
                    </FormControl>
                    <FormControl fullWidth margin="normal">
                        <InputLabel>Permission</InputLabel>
                        <Select value={permissionType} label="Permission" onChange={(e) => setPermissionType(e.target.value)}>
                            <MenuItem value="VIEW">View Only</MenuItem>
                            <MenuItem value="DOWNLOAD">Download Only</MenuItem>
                            <MenuItem value="BOTH">View and Download</MenuItem>
                        </Select>
                    </FormControl>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClosePermissionDialog}>Cancel</Button>
                    <Button onClick={() => handleAction(grantPermission, { username: selectedUserForPermission, fileId: selectedFileForPermission.id, permissionType })} variant="contained">Grant</Button>
                </DialogActions>
            </Dialog>
        </Container>
    );
};

export default AdminPage;
