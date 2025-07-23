package com.sensitivedata.logger.repositories;

import com.sensitivedata.logger.models.File;
import com.sensitivedata.logger.models.Permission;
import com.sensitivedata.logger.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * Checks if a permission exists for a user and file, matching any of the given permission types.
     * This is used to see if a user has VIEW (checks for VIEW or BOTH) or DOWNLOAD (checks for DOWNLOAD or BOTH) rights.
     */
    boolean existsByUserUsernameAndFileIdAndPermissionTypeIn(String username, Long fileId, List<Permission.PermissionType> types);

    /**
     * Deletes all permissions associated with a specific user and file.
     * This is used before granting a new permission to ensure there are no conflicting old ones.
     */
    void deleteByUserAndFile(User user, File file);

    /**
     * Finds all permissions granted for a specific file.
     * This is used by the admin panel to show who has access to what.
     */
    List<Permission> findByFileId(Long fileId);
}
