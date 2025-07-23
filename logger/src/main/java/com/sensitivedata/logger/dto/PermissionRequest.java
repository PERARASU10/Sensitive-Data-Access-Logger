package com.sensitivedata.logger.dto;


import com.sensitivedata.logger.models.Permission.PermissionType;
import lombok.Data;

@Data
public class PermissionRequest {
    private String username;
    private Long fileId;
    private PermissionType permissionType;
}