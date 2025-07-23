package com.sensitivedata.logger.services;

import com.sensitivedata.logger.models.File;
import com.sensitivedata.logger.models.Permission;
import com.sensitivedata.logger.models.User;
import com.sensitivedata.logger.repositories.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class FileStorageService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final FileRepository fileRepository;
    private final PermissionService permissionService; // Inject PermissionService

    @Transactional // Add this annotation
    public File storeFile(MultipartFile multipartFile, User uploader) throws IOException {
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        if (fileName.contains("..")) {
            throw new IOException("Filename contains invalid path sequence " + fileName);
        }

        Path targetLocation = Paths.get(uploadDir).resolve(fileName);
        Files.copy(multipartFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        File fileEntity = new File();
        fileEntity.setName(fileName);
        fileEntity.setFilePath(targetLocation.toString());
        fileEntity.setUploadedBy(uploader);

        File savedFile = fileRepository.save(fileEntity);

        // Automatically grant download permission to the uploader
        permissionService.grantPermission(uploader.getUsername(), savedFile.getId(), Permission.PermissionType.DOWNLOAD);

        return savedFile;
    }

    public Resource loadFileAsResource(Long fileId) throws IOException {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
        Path filePath = Paths.get(file.getFilePath()).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists()) {
            return resource;
        }
        throw new IOException("File not found: " + file.getName());
    }
}
