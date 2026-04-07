package com.abdulkhadirjallow.digitalwalletapi.dto;

import com.abdulkhadirjallow.digitalwalletapi.enums.Currency;
import com.abdulkhadirjallow.digitalwalletapi.enums.TransferStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
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

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferResponse {

    private String reference;
    private TransferStatus transferStatus;
    private String message;

    private String description;
    private SenderInfo senderInfo;
    private BigDecimal senderAmount;
    private BigDecimal recipientAmount;
    private BigDecimal fee;

    private Currency senderCurrency;
    private Currency recipientCurrency;

    private RecipientInfo recipientInfo;

    private LocalDateTime createdAt;
}
