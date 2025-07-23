package com.sensitivedata.logger.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

@Entity
@Getter      // Add this
@Setter      // Add this
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Role {
        USER, ADMIN;

        public String getAuthority() {
            return "ROLE_" + this.name();
        }
    }

    // Add this method for authorities
    public String[] getAuthorities() {
        return new String[]{role.getAuthority()};
    }
}