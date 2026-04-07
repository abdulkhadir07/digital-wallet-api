package com.abdulkhadirjallow.digitalwalletapi.repository;

import com.abdulkhadirjallow.digitalwalletapi.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    // look up a specific transaction by reference
    Optional<WalletTransaction> findByReference(String reference);

    // transaction history for a wallet
    List<WalletTransaction> findByWalletIdOrderByCreatedAtDesc(Long walletId);

    // check if they reference already exists
    boolean existsByReference(String reference);
}