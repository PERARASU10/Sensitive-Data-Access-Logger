package com.sensitivedata.logger.controllers;

import com.sensitivedata.logger.dto.FileDto;
import com.sensitivedata.logger.models.AccessLog;
import com.sensitivedata.logger.models.AccessLog.LogStatus;
import com.sensitivedata.logger.models.File;
import com.sensitivedata.logger.models.User;
import com.sensitivedata.logger.repositories.FileRepository;
import com.sensitivedata.logger.repositories.UserRepository;
import com.sensitivedata.logger.services.AccessLogService;
import com.sensitivedata.logger.services.FileStorageService;
import com.sensitivedata.logger.services.PermissionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final FileStorageService fileStorageService;
    private final PermissionService permissionService;
    private final AccessLogService accessLogService;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    @GetMapping
    public ResponseEntity<List<FileDto>> getFilesForUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(401).build();
        List<File> files = fileRepository.findFilesByUsernameWithPermission(userDetails.getUsername());
        return ResponseEntity.ok(files.stream().map(FileDto::fromEntity).collect(Collectors.toList()));
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        User uploader = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(fileStorageService.storeFile(file, uploader));
    }

    @GetMapping("/view/{fileId}")
    public ResponseEntity<Resource> viewFile(@PathVariable Long fileId, @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) throws IOException {
        String username = userDetails.getUsername();
        String ipAddress = request.getRemoteAddr();
        if (!permissionService.hasViewPermission(username, fileId)) {
            accessLogService.logAccess(username, fileId, AccessLog.ActionType.VIEW, ipAddress, AccessLog.LogStatus.DENIED);
            throw new SecurityException("No view permission for this file.");
        }

        Resource resource = fileStorageService.loadFileAsResource(fileId);
        accessLogService.logAccess(username, fileId, AccessLog.ActionType.VIEW, ipAddress, AccessLog.LogStatus.APPROVED);

        // --- THIS IS THE FIX ---
        // Determine content type and set it in the header
        String contentType = "application/octet-stream"; // Default type
        try {
            Path path = Paths.get(resource.getURI());
            contentType = Files.probeContentType(path);
        } catch (IOException e) {
            // Log error or handle, but continue with default type
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId, @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) throws IOException {
        String username = userDetails.getUsername();
        String ipAddress = request.getRemoteAddr();
        if (!permissionService.hasDownloadPermission(username, fileId)) {
            accessLogService.logAccess(username, fileId, AccessLog.ActionType.DOWNLOAD, ipAddress, LogStatus.DENIED);
            throw new SecurityException("No download permission for this file.");
        }
        Resource resource = fileStorageService.loadFileAsResource(fileId);
        accessLogService.logAccess(username, fileId, AccessLog.ActionType.DOWNLOAD, ipAddress, LogStatus.APPROVED);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
