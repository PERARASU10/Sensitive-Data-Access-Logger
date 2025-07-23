package com.sensitivedata.logger.controllers;

import com.sensitivedata.logger.dto.FileDto;
import com.sensitivedata.logger.dto.PermissionDto; // Import new DTO
import com.sensitivedata.logger.dto.PermissionRequest;
import com.sensitivedata.logger.dto.UserDto;
import com.sensitivedata.logger.models.Permission;
import com.sensitivedata.logger.models.User;
import com.sensitivedata.logger.repositories.FileRepository;
import com.sensitivedata.logger.repositories.PermissionRepository;
import com.sensitivedata.logger.repositories.UserRepository;
import com.sensitivedata.logger.services.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final PermissionService permissionService;
    private final PermissionRepository permissionRepository;

    // --- User Management ---
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll().stream().map(UserDto::fromEntity).collect(Collectors.toList()));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}/promote")
    public ResponseEntity<UserDto> promoteUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(User.Role.ADMIN);
        return ResponseEntity.ok(UserDto.fromEntity(userRepository.save(user)));
    }

    // --- File Management ---
    @GetMapping("/files")
    public ResponseEntity<List<FileDto>> getAllFiles() {
        return ResponseEntity.ok(fileRepository.findAll().stream().map(FileDto::fromEntity).collect(Collectors.toList()));
    }

    // --- Permission Management ---
    @PostMapping("/permissions")
    public ResponseEntity<Void> grantPermission(@RequestBody PermissionRequest request) {
        permissionService.grantPermission(request.getUsername(), request.getFileId(), request.getPermissionType());
        return ResponseEntity.ok().build();
    }

    // THIS IS THE NEW ENDPOINT FOR THE "All Permissions" TAB
    @GetMapping("/permissions")
    public ResponseEntity<List<PermissionDto>> getAllPermissions() {
        List<PermissionDto> permissions = permissionRepository.findAll().stream()
                .map(PermissionDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(permissions);
    }

    // THIS FIXES THE REDIRECT. The path is simplified to avoid potential conflicts.
    @GetMapping("/permissions/file/{fileId}")
    public ResponseEntity<List<PermissionDto>> getFilePermissions(@PathVariable Long fileId) {
        List<PermissionDto> permissions = permissionRepository.findByFileId(fileId).stream()
                .map(PermissionDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(permissions);
    }

    @DeleteMapping("/permissions/{permissionId}")
    public ResponseEntity<Void> revokePermission(@PathVariable Long permissionId) {
        permissionRepository.deleteById(permissionId);
        return ResponseEntity.noContent().build();
    }
}
