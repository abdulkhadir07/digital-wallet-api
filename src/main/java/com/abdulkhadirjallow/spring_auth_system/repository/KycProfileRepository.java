package com.abdulkhadirjallow.spring_auth_system.repository;

import com.abdulkhadirjallow.spring_auth_system.entity.KycProfile;
import com.abdulkhadirjallow.spring_auth_system.entity.User;
import com.abdulkhadirjallow.spring_auth_system.enums.KycStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KycProfileRepository extends JpaRepository<KycProfile, Long> {

    // Find a Kyc profile by the user Object
    Optional<KycProfile> findByUserId(Long userId);

    // Check if user is already Kyc verified
    boolean existsByUser(User user);

    // Check if id Number already exists
    boolean existsByIdNumber(String idNumber);

    // Find all Kyc profiles with a specific status(For admin dashboard)
    List<KycProfile> findByStatus(KycStatus status);
}
