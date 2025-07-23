package com.sensitivedata.logger.dto;

import com.sensitivedata.logger.models.AccessLog;
import lombok.Data;

import java.time.Instant;

@Data
public class AccessLogDto {
    private Long id;
    private String username;
    private String fileName;
    private AccessLog.ActionType actionType;
    private Instant timestamp;
    private String ipAddress;

    public static AccessLogDto fromEntity(AccessLog log) {
        AccessLogDto dto = new AccessLogDto();
        dto.setId(log.getId());
        dto.setUsername(log.getUser() != null ? log.getUser().getUsername() : "N/A");
        dto.setFileName(log.getFile() != null ? log.getFile().getName() : "N/A");
        dto.setActionType(log.getActionType());
        dto.setTimestamp(log.getTimestamp());
        dto.setIpAddress(log.getIpAddress());
        return dto;
    }
}
