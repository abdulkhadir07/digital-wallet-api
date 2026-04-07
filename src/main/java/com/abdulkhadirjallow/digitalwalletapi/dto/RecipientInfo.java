package com.abdulkhadirjallow.digitalwalletapi.dto;

import com.abdulkhadirjallow.digitalwalletapi.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class RecipientInfo {
    private String recipientName;
    private String recipientPhoneNumber;

    // static method
   public static RecipientInfo from(User user) {
        return new RecipientInfo(
                user.getFirstName() + " " + user.getLastName(),
                user.getPhoneNumber()
        );
       }
}
