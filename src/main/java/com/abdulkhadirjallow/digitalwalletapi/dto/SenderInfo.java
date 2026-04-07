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
public class SenderInfo {

    private String senderName;
    private String senderPhoneNumber;

    // static method
    public static SenderInfo from(User user) {
        return new SenderInfo(
                user.getFirstName() + " " + user.getLastName(),
                user.getPhoneNumber()
        );
    }
}
