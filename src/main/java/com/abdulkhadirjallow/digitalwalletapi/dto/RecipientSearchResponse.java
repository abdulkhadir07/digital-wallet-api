package com.abdulkhadirjallow.digitalwalletapi.dto;

import com.abdulkhadirjallow.digitalwalletapi.entity.User;
import com.abdulkhadirjallow.digitalwalletapi.enums.Country;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class RecipientSearchResponse {
    private String recipientName;
    private String recipientPhoneNumber;
    private Country country;

    public static RecipientSearchResponse from(User user) {
        return new RecipientSearchResponse(
                user.getFirstName() + " " + user.getLastName(),
                user.getPhoneNumber(),
                user.getCountry()
        );
    }
}
