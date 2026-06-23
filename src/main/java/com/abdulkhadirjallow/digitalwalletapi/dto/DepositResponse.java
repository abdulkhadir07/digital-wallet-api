package com.abdulkhadirjallow.digitalwalletapi.dto;

import com.abdulkhadirjallow.digitalwalletapi.enums.Currency;
import com.abdulkhadirjallow.digitalwalletapi.enums.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepositResponse {
    private String message;
    private BigDecimal amount;
    private BigDecimal newBalance;
    private Currency currency;
    private PaymentMethod paymentMethod;
    private String transactionReference;
    private LocalDateTime createdAt;
}
