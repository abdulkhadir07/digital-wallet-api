package com.abdulkhadirjallow.digitalwalletapi.dto;

import com.abdulkhadirjallow.digitalwalletapi.enums.Currency;
import com.abdulkhadirjallow.digitalwalletapi.enums.TransferType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class TransferQuoteResponse {

    private String recipientPhoneNumber;
    private RecipientInfo recipientInfo;
    private BigDecimal senderAmount;
    private BigDecimal recipientAmount;
    private BigDecimal fee;
    private BigDecimal totalDebitAmount;
    private BigDecimal exchangeRate;
    private BigDecimal retailRate;
    private BigDecimal fxMarkupPercent;
    private Currency senderCurrency;
    private Currency recipientCurrency;
    private TransferType transferType;
    private String message;
}
