package com.abdulkhadirjallow.spring_auth_system.dto;

import com.abdulkhadirjallow.spring_auth_system.enums.TransactionSource;
import com.abdulkhadirjallow.spring_auth_system.enums.TransactionStatus;
import com.abdulkhadirjallow.spring_auth_system.enums.TransactionType;
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
