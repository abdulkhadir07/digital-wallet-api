package com.abdulkhadirjallow.spring_auth_system.repository;

import com.abdulkhadirjallow.spring_auth_system.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    Optional<Transfer> findByReference(String reference);

    // return all transfers for a specific user
    List<Transfer> findBySenderUserIdOrderByCreatedAtDesc(Long userId);
    List<Transfer> findByRecipientUserIdOrderByCreatedAtDesc(Long userId);

    // check if a specific reference exist
    boolean existsByReference(String reference);
}
