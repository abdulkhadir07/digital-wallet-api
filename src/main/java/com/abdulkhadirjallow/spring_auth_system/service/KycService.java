package com.abdulkhadirjallow.spring_auth_system.service;

import com.abdulkhadirjallow.spring_auth_system.dto.KycSubmitRequest;
import com.abdulkhadirjallow.spring_auth_system.entity.KycProfile;
import com.abdulkhadirjallow.spring_auth_system.entity.User;
import com.abdulkhadirjallow.spring_auth_system.enums.KycStatus;
import com.abdulkhadirjallow.spring_auth_system.exception.BadRequestException;
import com.abdulkhadirjallow.spring_auth_system.repository.KycProfileRepository;
import com.abdulkhadirjallow.spring_auth_system.repository.UserRepository;
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
        kycProfile.setDateOfBirth(kycSubmitRequest.getDateOfBirth());
        kycProfile.setIdType(kycSubmitRequest.getIdType());
        kycProfile.setIdNumber(kycSubmitRequest.getIdNumber().trim().toUpperCase());
        kycProfile.setExpiryDate(kycSubmitRequest.getExpiryDate());
        kycProfile.setStreetAddress(kycSubmitRequest.getStreetAddress().trim());
        kycProfile.setCity(kycSubmitRequest.getCity().trim());
        kycProfile.setState(kycSubmitRequest.getState() != null ? kycSubmitRequest.getState().trim() : null);
        kycProfile.setPostalCode(kycSubmitRequest.getPostalCode().trim());
        kycProfile.setCountry(kycSubmitRequest.getCountry().trim());
        kycProfile.setStatus(KycStatus.PENDING);
        kycProfile.setSubmittedAt(LocalDateTime.now());

        // Save to Database
        return kycProfileRepository.save(kycProfile);
    }
}
