package com.abdulkhadirjallow.spring_auth_system.entity;

import com.abdulkhadirjallow.spring_auth_system.enums.Currency;
import com.abdulkhadirjallow.spring_auth_system.enums.WalletStatus;
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
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletStatus walletStatus = WalletStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Wallet(User user) {
        this.user = user;
        this.currency = user.getCountry().getDefaultCurrency();
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
