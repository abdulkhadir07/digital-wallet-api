package com.abdulkhadirjallow.digitalwalletapi.dto;

import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class RegisterResponse {
    private String message;
    private String verificationCode;
}
