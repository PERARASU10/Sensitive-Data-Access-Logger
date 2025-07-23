package com.sensitivedata.logger.dto;

import com.sensitivedata.logger.models.File;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {
    private Long id;
    private String name;
    private LocalDateTime uploadTime;

    public static FileDto fromEntity(File file) {
        return new FileDto(
                file.getId(),
                file.getName(),
                file.getUploadTime()
        );
    }
}
