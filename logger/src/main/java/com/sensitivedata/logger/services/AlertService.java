package com.sensitivedata.logger.services;

import com.sensitivedata.logger.models.Alert;
import com.sensitivedata.logger.models.User;
import com.sensitivedata.logger.models.AccessLog.ActionType;
import com.sensitivedata.logger.repositories.AlertRepository;
import com.sensitivedata.logger.repositories.UserRepository;
import com.sensitivedata.logger.repositories.AccessLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {
    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final AccessLogRepository accessLogRepository;

    @Transactional
    public Alert createAlert(User user, String message) {
        Alert alert = new Alert();
        alert.setUser(user);
        alert.setMessage(message);
        return alertRepository.save(alert);
    }

    @Scheduled(fixedRate = 300000) // 5 minutes
    @Transactional
    public void checkMassDownloads() {
        Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);
        List<User> users = userRepository.findAll();

        users.forEach(user -> {
            long downloadCount = accessLogRepository.countByUserAndActionTypeAndTimestampAfter(
                    user,
                    ActionType.DOWNLOAD,
                    oneHourAgo
            );
            if (downloadCount > 20) {
                createAlert(user, "Mass download detected: " + downloadCount + " downloads in last hour");
            }
        });
    }
}