package com.sensitivedata.logger.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
}