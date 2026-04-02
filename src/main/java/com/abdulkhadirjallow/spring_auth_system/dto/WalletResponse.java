package com.abdulkhadirjallow.spring_auth_system.dto;

import com.abdulkhadirjallow.spring_auth_system.enums.Currency;
import com.abdulkhadirjallow.spring_auth_system.enums.WalletStatus;
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
public class WalletResponse {

    private Currency currency;
    private BigDecimal balance;
    private WalletStatus walletStatus;
    private LocalDateTime createdAt;

}
