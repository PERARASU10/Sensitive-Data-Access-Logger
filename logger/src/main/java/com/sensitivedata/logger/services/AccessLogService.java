package com.sensitivedata.logger.services;

import com.sensitivedata.logger.models.AccessLog;
import com.sensitivedata.logger.models.AccessLog.ActionType;
import com.sensitivedata.logger.models.AccessLog.LogStatus; // Import the new enum
import com.sensitivedata.logger.models.File;
import com.sensitivedata.logger.models.User;
import com.sensitivedata.logger.repositories.AccessLogRepository;
import com.sensitivedata.logger.repositories.FileRepository;
import com.sensitivedata.logger.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccessLogService {
    private final AccessLogRepository accessLogRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    /**
     * Logs an access attempt for a file.
     * @param username The user attempting the action.
     * @param fileId The ID of the file being accessed.
     * @param actionType The type of action (VIEW or DOWNLOAD).
     * @param ipAddress The IP address of the user.
     * @param status The result of the access attempt (APPROVED or DENIED).
     */
    @Transactional
    public void logAccess(String username, Long fileId, ActionType actionType, String ipAddress, LogStatus status) {
        // Find user and file. They might be null if the ID is invalid, which is fine for a DENIED log.
        User user = userRepository.findByUsername(username).orElse(null);
        File file = fileRepository.findById(fileId).orElse(null);

        AccessLog log = new AccessLog();
        log.setUser(user);
        log.setFile(file);
        log.setActionType(actionType);
        log.setIpAddress(ipAddress);
        log.setStatus(status); // Set the status of the log

        accessLogRepository.save(log);
    }
}
