package com.sensitivedata.logger.repositories;

import com.sensitivedata.logger.models.AccessLog;
import com.sensitivedata.logger.models.User;
import com.sensitivedata.logger.models.AccessLog.ActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
    Long countByUserAndActionTypeAndTimestampAfter(User user, ActionType actionType, Instant timestamp);
    List<AccessLog> findByUserUsername(String username);
}
