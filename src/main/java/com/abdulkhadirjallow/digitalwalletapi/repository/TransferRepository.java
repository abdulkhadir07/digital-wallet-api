package com.abdulkhadirjallow.digitalwalletapi.repository;

import com.abdulkhadirjallow.digitalwalletapi.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    // return a specific reference for a specific user
    Optional<Transfer> findByReference(String reference);

    // return all transfers for a specific user
    List<Transfer> findBySenderUserIdOrderByCreatedAtDesc(Long senderUserId);
    List<Transfer> findByRecipientUserIdOrderByCreatedAtDesc(Long recipientUserId);

    @Query("SELECT t FROM Transfer t WHERE t.senderUser.id = :userId OR t.recipientUser.id = :userId ORDER BY t.createdAt DESC")
    List<Transfer> findAllByUserId(@Param("userId") Long userId);

    // check if a specific reference exist
    boolean existsByReference(String reference);
}
