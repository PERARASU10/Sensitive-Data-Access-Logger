package com.sensitivedata.logger.controllers;

import com.sensitivedata.logger.dto.AccessLogDto;
import com.sensitivedata.logger.repositories.AccessLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {
    private final AccessLogRepository accessLogRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // <-- ENSURE THIS IS CORRECT
    public ResponseEntity<List<AccessLogDto>> getAllLogs() {
        List<AccessLogDto> logs = accessLogRepository.findAll().stream()
                .map(AccessLogDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(logs);
    }
}
