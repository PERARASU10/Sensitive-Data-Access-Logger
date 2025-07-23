package com.sensitivedata.logger.dto;

import com.sensitivedata.logger.models.Permission;
import lombok.Data;

@Data
public class PermissionDto {
    private Long id;
    private String username;
    private String fileName;
    private Long fileId;
    private Permission.PermissionType permissionType;

    public static PermissionDto fromEntity(Permission permission) {
        PermissionDto dto = new PermissionDto();
        dto.setId(permission.getId());
        dto.setUsername(permission.getUser().getUsername());
        dto.setFileName(permission.getFile().getName());
        dto.setFileId(permission.getFile().getId());
        dto.setPermissionType(permission.getPermissionType());
        return dto;
    }
}
