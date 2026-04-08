package com.abdulkhadirjallow.digitalwalletapi.dto;

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

    //First name, last name, DOB and country should be derived from the user registration(user should confirm)

    @NotBlank(message = "ID type(Passport, ID Card, Driver's License or Voter's card is required")
    private String idType;

    @NotNull(message = "ID number is required")
    private String idNumber;

    // third part API will do its job
    @NotNull(message = "Expiry date is required")
    @Future
    private LocalDate expiryDate;

    @NotBlank(message = "Street address is required")
    private String streetAddress;

    @NotBlank(message = "City is required")
    private String city;

    private String state;

    @NotBlank(message = "Zip code is required")
    private String postalCode;

    @NotBlank(message = "Country is required")
    private String country;

    /*private String idFrontImage;
    private String idBackImage;
    private String selfieUrl;*/

}
