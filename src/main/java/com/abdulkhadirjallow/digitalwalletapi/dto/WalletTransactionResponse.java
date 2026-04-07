package com.abdulkhadirjallow.digitalwalletapi.dto;

import com.abdulkhadirjallow.digitalwalletapi.enums.TransactionSource;
import com.abdulkhadirjallow.digitalwalletapi.enums.TransactionStatus;
import com.abdulkhadirjallow.digitalwalletapi.enums.TransactionType;
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
public class WalletTransactionResponse {

    private String reference;
    private String description;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private TransactionSource transactionSource;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private LocalDateTime createdAt;
}
