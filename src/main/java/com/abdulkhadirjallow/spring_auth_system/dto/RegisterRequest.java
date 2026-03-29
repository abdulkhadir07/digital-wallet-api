package com.abdulkhadirjallow.spring_auth_system.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Phone Number is required")
    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Email(message = "Please enter a valid email address")
    @Column(nullable = true, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 30, message = "Password must be at least 8 characters!")
    private String password;

}
