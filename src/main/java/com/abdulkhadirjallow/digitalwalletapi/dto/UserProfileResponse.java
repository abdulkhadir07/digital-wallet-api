package com.abdulkhadirjallow.digitalwalletapi.dto;

import com.abdulkhadirjallow.digitalwalletapi.entity.User;
import com.abdulkhadirjallow.digitalwalletapi.enums.Country;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Country country;
    private LocalDate dateOfBirth;
    private boolean verified;

    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getCountry(),
                user.getDateOfBirth(),
                user.isVerified()
        );
    }
}
