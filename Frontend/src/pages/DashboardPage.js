import React, { useEffect, useState } from 'react';
import { Container, Typography, Box, Paper, Button } from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import { getFiles, downloadFile, viewFile } from '../api/fileApi'; // Import viewFile
import ErrorMessage from '../components/common/ErrorMessage';
import LoadingSpinner from '../components/common/LoadingSpinner';

const DashboardPage = () => {
  const [files, setFiles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchFiles = async () => {
      setLoading(true);
      try {
        setFiles(await getFiles());
      } catch (err) {
        setError('Could not fetch files.');
      } finally {
        setLoading(false);
      }
    };
    fetchFiles();
  }, []);

  const handleAction = async (action, fileId, fileName) => {
    setError(''); // Clear previous errors
    try {
      const response = await action(fileId);
      const blob = new Blob([response.data], { type: response.headers['content-type'] });
      const fileURL = URL.createObjectURL(blob);

      if (action === downloadFile) {
        // Create a link and click it to trigger download
        const link = document.createElement('a');
        link.href = fileURL;
        link.setAttribute('download', fileName);
        document.body.appendChild(link);
        link.click();
        link.remove();
      } else {
        // Open in a new tab for viewing
        window.open(fileURL, '_blank');
      }
    } catch (err) {
      setError(`Action failed for ${fileName}. You may not have permission.`);
    }
  };

  const columns = [
    { field: 'id', headerName: 'ID', width: 90 },
    { field: 'name', headerName: 'File Name', flex: 1 },
    {
      field: 'uploadTime', headerName: 'Upload Time', width: 200,
      valueGetter: (params) => new Date(params.row.uploadTime).toLocaleString(),
    },
    {
      field: 'actions', headerName: 'Actions', width: 250, sortable: false,
      renderCell: (params) => (
        <Box sx={{ display: 'flex', gap: 1 }}>
          <Button variant="contained" size="small" onClick={() => handleAction(viewFile, params.row.id, params.row.name)}>View</Button>
          <Button variant="outlined" size="small" onClick={() => handleAction(downloadFile, params.row.id, params.row.name)}>Download</Button>
        </Box>
      ),
    },
  ];

  if (loading) return <LoadingSpinner />;

  return (
    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Paper sx={{ p: 3, height: '70vh', width: '100%' }}>
        <Typography variant="h4" gutterBottom>My Files</Typography>
        <ErrorMessage message={error} />
        <Box sx={{ height: 'calc(100% - 60px)', width: '100%', mt: 2 }}>
          <DataGrid rows={files} columns={columns} pageSizeOptions={[10, 25]} checkboxSelection disableRowSelectionOnClick />
        </Box>
      </Paper>
    </Container>
  );
};

export default DashboardPage;
