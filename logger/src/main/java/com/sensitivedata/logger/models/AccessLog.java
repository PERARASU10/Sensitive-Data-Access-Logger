package com.sensitivedata.logger.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Entity
@Data
public class AccessLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private File file;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @Enumerated(EnumType.STRING)
    private LogStatus status; // <-- NEW FIELD

    private Instant timestamp = Instant.now();
    private String ipAddress;

    public enum ActionType {
        VIEW, DOWNLOAD
    }

    public enum LogStatus { // <-- NEW ENUM
        APPROVED, DENIED
    }
}
