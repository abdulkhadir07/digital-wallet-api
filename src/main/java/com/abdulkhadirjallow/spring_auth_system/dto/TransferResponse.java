package com.abdulkhadirjallow.spring_auth_system.dto;

import com.abdulkhadirjallow.spring_auth_system.enums.Currency;
import com.abdulkhadirjallow.spring_auth_system.enums.TransferStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class TransferResponse {

    private String reference;
    private TransferStatus transferStatus;
    private String message;

    private BigDecimal senderAmount;
    private BigDecimal recipientAmount;
    private BigDecimal fee;

    private Currency senderCurrency;
    private Currency recipientCurrency;

    private RecipientInfo recipientInfo;

    private LocalDateTime createdAt;
}
