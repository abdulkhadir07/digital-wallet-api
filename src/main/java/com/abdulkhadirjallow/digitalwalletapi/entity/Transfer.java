package com.abdulkhadirjallow.digitalwalletapi.entity;

import com.abdulkhadirjallow.digitalwalletapi.enums.Currency;
import com.abdulkhadirjallow.digitalwalletapi.enums.TransferStatus;
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
@Table(name = "transfers")
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus transferStatus = TransferStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_user_id", nullable = false)
    private User senderUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_user_id", nullable = false)
    private User recipientUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_wallet_id", nullable = false)
    private Wallet senderWallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_wallet_id", nullable = false)
    private Wallet recipientWallet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency senderCurrency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency recipientCurrency;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal senderAmount;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal recipientAmount;

    @Column(precision = 18, scale = 4)
    private BigDecimal fee;

    @Column(precision = 18, scale = 4)
    private BigDecimal exchangeRate;

    private String description;

    @Column(nullable = false, unique = true, updatable = false)
    private String reference;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onPrePersist() {
        if (createdAt == null) {createdAt = LocalDateTime.now();}

        if (reference == null) {reference = "TRF-" +  java.util.UUID.randomUUID().toString().substring(0,8).toUpperCase();}

        if (transferStatus == null )  {transferStatus = TransferStatus.PENDING;}
    }
}
