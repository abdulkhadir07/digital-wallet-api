package com.abdulkhadirjallow.spring_auth_system.dto;

import com.abdulkhadirjallow.spring_auth_system.entity.User;
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
