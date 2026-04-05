package com.abdulkhadirjallow.spring_auth_system.repository;

import com.abdulkhadirjallow.spring_auth_system.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    // return a specific reference for a specific user
    Optional<Transfer> findByReference(String reference);
    Optional<Transfer> findBySenderUserId(Long senderUserId);

    // return all transfers for a specific user
    List<Transfer> findBySenderUserIdOrderByCreatedAtDesc(Long senderUserId);
    List<Transfer> findByRecipientUserIdOrderByCreatedAtDesc(Long recipientUserId);

    // check if a specific reference exist
    boolean existsByReference(String reference);
}
