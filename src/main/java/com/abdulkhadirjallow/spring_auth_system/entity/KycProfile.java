package com.abdulkhadirjallow.spring_auth_system.entity;

import com.abdulkhadirjallow.spring_auth_system.enums.KycStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KycProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Document Details

    @NotBlank(message = "ID type is required")
    @Column(nullable = false)
    private String idType; // PASSPORT, ID CARD, DRIVER'S LICENSE, VOTER'S CARD

    @NotBlank(message = " ID number is required")
    @Column(nullable = false, unique = true)
    private String idNumber; // Encrypt this

    @NotNull(message = "Expiry date is required")
    @Column(nullable = false)
    private LocalDate expiryDate;

    // Structured Address (Required for Global Remittance)
    @NotBlank(message = " Street address is required")
    @Column(nullable = false)
    private String streetAddress;

    @NotBlank(message = "City is required")
    @Column(nullable = false)
    private String city;

    @Column(nullable = true)
    private String state;

    @NotBlank(message = "Postal Code is required")
    @Column(nullable = false)
    private String postalCode;

    // Files (URLs to secure storage)
    private String idFrontImageUrl;
    private String idBackImageUrl;
    private String selfieUrl;

    // Status tracking
    @Enumerated(EnumType.STRING)
    @Column (nullable = false)
    private KycStatus status = KycStatus.UNVERIFIED;

    @Column(nullable = false)
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private String rejectionReason;

    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
    }

}
