package com.sensitivedata.logger.dto;

import com.sensitivedata.logger.models.Alert;
import lombok.Data;

import java.time.Instant;

@Data
public class AlertDto {
    private Long id;
    private String username;
    private String message;
    private Instant createdAt;

    public static AlertDto fromEntity(Alert alert) {
        AlertDto dto = new AlertDto();
        dto.setId(alert.getId());
        dto.setUsername(alert.getUser() != null ? alert.getUser().getUsername() : "N/A");
        dto.setMessage(alert.getMessage());
        dto.setCreatedAt(alert.getCreatedAt());
        return dto;
    }
}
