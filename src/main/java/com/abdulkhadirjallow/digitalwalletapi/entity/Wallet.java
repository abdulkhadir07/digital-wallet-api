package com.abdulkhadirjallow.digitalwalletapi.entity;

import com.abdulkhadirjallow.digitalwalletapi.enums.Currency;
import com.abdulkhadirjallow.digitalwalletapi.enums.WalletStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletStatus walletStatus;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Wallet(User user) {

        if(user == null || user.getCountry() == null) {
            throw new IllegalArgumentException("User and country are required");
        }
        this.user = user;
        this.currency = user.getCountry().getDefaultCurrency();

        //DEV ONLY: initialize with default balance for testing purposes only
        this.balance = new BigDecimal("1000.00");

        this.walletStatus = WalletStatus.ACTIVE;
    }

    @PrePersist
    @PreUpdate
    protected void onCreate() {
        if(this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }
}
