package com.abdulkhadirjallow.digitalwalletapi.service;

import com.abdulkhadirjallow.digitalwalletapi.dto.KycSubmitRequest;
import com.abdulkhadirjallow.digitalwalletapi.entity.KycProfile;
import com.abdulkhadirjallow.digitalwalletapi.entity.User;
import com.abdulkhadirjallow.digitalwalletapi.enums.KycStatus;
import com.abdulkhadirjallow.digitalwalletapi.exception.BadRequestException;
import com.abdulkhadirjallow.digitalwalletapi.repository.KycProfileRepository;
import com.abdulkhadirjallow.digitalwalletapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class KycService {
    private final KycProfileRepository kycProfileRepository;
    private final UserRepository userRepository;

    public KycService(KycProfileRepository kycProfileRepository, UserRepository userRepository) {
        this.kycProfileRepository = kycProfileRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public KycProfile submitKyc(Long userId, KycSubmitRequest kycSubmitRequest) {

        // Loads the user entity from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Authenticated user not found"));

        // check if the user account is already verified
        if(kycProfileRepository.existsByUser(user)) {
            throw new BadRequestException("Kyc profile already exists for this user.");
        }

        // check if the ID number is already use
        if (kycProfileRepository.existsByIdNumber(kycSubmitRequest.getIdNumber().trim().toUpperCase())) {
            throw new BadRequestException("Id number is already in use.");
        }

        // Map DTO to Entity
        KycProfile kycProfile = new KycProfile();

        kycProfile.setUser(user);
        kycProfile.setIdType(kycSubmitRequest.getIdType());
        kycProfile.setIdNumber(kycSubmitRequest.getIdNumber().trim().toUpperCase());
        kycProfile.setExpiryDate(kycSubmitRequest.getExpiryDate());
        kycProfile.setStreetAddress(kycSubmitRequest.getStreetAddress().trim());
        kycProfile.setCity(kycSubmitRequest.getCity().trim());
        kycProfile.setState(kycSubmitRequest.getState() != null ? kycSubmitRequest.getState().trim() : null);
        kycProfile.setPostalCode(kycSubmitRequest.getPostalCode().trim());
        kycProfile.setStatus(KycStatus.VERIFIED);
        kycProfile.setSubmittedAt(LocalDateTime.now());

        // Save to Database
        return kycProfileRepository.save(kycProfile);
    }
}
