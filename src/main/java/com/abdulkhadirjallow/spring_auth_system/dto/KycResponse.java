package com.abdulkhadirjallow.spring_auth_system.dto;

import com.abdulkhadirjallow.spring_auth_system.enums.KycStatus;

import java.time.LocalDateTime;

public class KycResponse {
    private String message;
    private KycStatus status;
    private LocalDateTime submittedAt;
}
