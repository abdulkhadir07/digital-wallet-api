package com.abdulkhadirjallow.digitalwalletapi.dto;

import com.abdulkhadirjallow.digitalwalletapi.enums.Country;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @NotNull( message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotNull(message = "Country is required")
    private Country country;

    @NotBlank(message = "Phone Number is required")
    private String phoneNumber;

    @Email(message = "Please enter a valid email address")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 30, message = "Password must be at least 8 characters!")
    private String password;
}
