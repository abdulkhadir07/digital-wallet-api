package com.abdulkhadirjallow.spring_auth_system.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KycSubmitRequest {

    //First and last name should be derived from the user registration(user should confirm)

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth;

    @NotBlank(message = "ID type(Passport, ID Card, Driver's License or Voter's card is required")
    private String idType;

    //
    @NotNull(message = "ID number is required")
    @Column(nullable = false,unique = true)
    private String idNumber;

    // third part API will do its job
    @NotNull(message = "Expiry date is required")
    @Future
    private LocalDate expiryDate;

    @NotBlank(message = "Street address is required")
    @Column(nullable = false)
    private String streetAddress;

    @NotBlank(message = "City is required")
    @Column(nullable = false)
    private String city;

    private String state;

    @NotBlank(message = "Zip code is required")
    @Column(nullable = false)
    private String postalCode;

    @NotBlank(message = "Country is required")
    @Column(nullable = false)
    private String country;

    /*private String idFrontImage;
    private String idBackImage;
    private String selfieUrl;*/

}
