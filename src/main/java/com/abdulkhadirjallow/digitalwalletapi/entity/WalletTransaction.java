package com.abdulkhadirjallow.digitalwalletapi.entity;

import com.abdulkhadirjallow.digitalwalletapi.enums.TransactionSource;
import com.abdulkhadirjallow.digitalwalletapi.enums.TransactionStatus;
import com.abdulkhadirjallow.digitalwalletapi.enums.TransactionType;
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
@Table(name = "wallet_transactions")
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    @Column (nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column (nullable = false)
    private TransactionSource transactionSource;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus transactionStatus;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal balanceBefore;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal balanceAfter;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal amount;

    private String description;

    @Column(nullable = false, unique = true, updatable = false)
    private String reference;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();

        // Generate a random reference Id
        if (this.reference == null) {
            this.reference = "TXN-" + java.util.UUID.randomUUID().toString().substring(0,8).toUpperCase();
        }
    }
}
