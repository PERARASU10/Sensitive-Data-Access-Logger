package com.sensitivedata.logger.controllers;

import com.sensitivedata.logger.dto.AlertDto;
import com.sensitivedata.logger.repositories.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {
    private final AlertRepository alertRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // <-- ENSURE THIS IS CORRECT
    public ResponseEntity<List<AlertDto>> getAllAlerts() {
        List<AlertDto> alerts = alertRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(AlertDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(alerts);
    }
}
