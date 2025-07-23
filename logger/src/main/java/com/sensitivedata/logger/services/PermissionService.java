package com.sensitivedata.logger.services;

import com.sensitivedata.logger.models.File;
import com.sensitivedata.logger.models.Permission;
import com.sensitivedata.logger.models.Permission.PermissionType;
import com.sensitivedata.logger.models.User;
import com.sensitivedata.logger.repositories.FileRepository;
import com.sensitivedata.logger.repositories.PermissionRepository;
import com.sensitivedata.logger.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    /**
     * Checks if a user has permission to view a file.
     * A user can view if they have VIEW or BOTH permission.
     */
    @Transactional(readOnly = true)
    public boolean hasViewPermission(String username, Long fileId) {
        return permissionRepository.existsByUserUsernameAndFileIdAndPermissionTypeIn(
                username, fileId, List.of(PermissionType.VIEW, PermissionType.BOTH));
    }

    /**
     * Checks if a user has permission to download a file.
     * A user can download if they have DOWNLOAD or BOTH permission.
     */
    @Transactional(readOnly = true)
    public boolean hasDownloadPermission(String username, Long fileId) {
        return permissionRepository.existsByUserUsernameAndFileIdAndPermissionTypeIn(
                username, fileId, List.of(PermissionType.DOWNLOAD, PermissionType.BOTH));
    }

    /**
     * Grants a new permission to a user for a file.
     * It first revokes any existing permissions for that user/file pair to prevent conflicts.
     */
    @Transactional
    public void grantPermission(String username, Long fileId, PermissionType type) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found: " + fileId));

        // Remove existing permissions for this user/file to avoid conflicts
        permissionRepository.deleteByUserAndFile(user, file);

        // Grant the new permission, if one is provided
        if (type != null) {
            Permission permission = new Permission();
            permission.setUser(user);
            permission.setFile(file);
            permission.setPermissionType(type);
            permissionRepository.save(permission);
        }
    }
}
