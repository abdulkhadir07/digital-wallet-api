package com.abdulkhadirjallow.digitalwalletapi.service;

import com.abdulkhadirjallow.digitalwalletapi.entity.User;
import com.abdulkhadirjallow.digitalwalletapi.entity.Wallet;
import com.abdulkhadirjallow.digitalwalletapi.entity.WalletTransaction;
import com.abdulkhadirjallow.digitalwalletapi.enums.TransactionSource;
import com.abdulkhadirjallow.digitalwalletapi.enums.TransactionStatus;
import com.abdulkhadirjallow.digitalwalletapi.enums.TransactionType;
import com.abdulkhadirjallow.digitalwalletapi.enums.WalletStatus;
import com.abdulkhadirjallow.digitalwalletapi.exception.BadRequestException;
import com.abdulkhadirjallow.digitalwalletapi.repository.UserRepository;
import com.abdulkhadirjallow.digitalwalletapi.repository.WalletRepository;
import com.abdulkhadirjallow.digitalwalletapi.repository.WalletTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final UserRepository userRepository;

    public WalletService(WalletRepository walletRepository, WalletTransactionRepository walletTransactionRepository, UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Wallet createWallet(Long userId) {

        // find the user
       User user = userRepository.findById(userId)
               .orElseThrow(()-> new BadRequestException("Authenticated user not found"));

        // check if wallet already exists for user
        if(walletRepository.existsByUserId(userId)) {
            throw new BadRequestException("Wallet already exists for user");
        }

        Wallet wallet = new Wallet(user);
        return walletRepository.save(wallet);
    }

    // Load the user wallet
    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(()-> new BadRequestException("User wallet not found"));
    }

    @Transactional
    public WalletTransaction debitWallet(Long userId,BigDecimal amount,TransactionSource transactionSource, String description) {

        Wallet wallet = walletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(()-> new BadRequestException("User wallet not found"));

        if(wallet.getWalletStatus() == WalletStatus.FROZEN) {
            throw new BadRequestException("Your wallet is FROZEN");
        }

        // check amount
        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than zero");
        }

        // check if user wallet have enough funds to debit their account
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException("You have insufficient funds");
        }

        //
        BigDecimal balanceBefore = wallet.getBalance();
        BigDecimal balanceAfter = balanceBefore.subtract(amount);

        // Update actual wallet
        wallet.setBalance(balanceAfter);
        walletRepository.save(wallet);

        return createTransaction(wallet,TransactionType.DEBIT,transactionSource,amount,balanceBefore,balanceAfter,description);
    }

    @Transactional
    public WalletTransaction creditWallet(Long userId, BigDecimal amount, TransactionSource transactionSource,String description) {

        Wallet wallet = walletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new BadRequestException("User wallet not found"));

        if(wallet.getWalletStatus() == WalletStatus.FROZEN) {
            throw new BadRequestException("Your wallet is FROZEN");
        }

        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than zero");
        }

        BigDecimal balanceBefore = wallet.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(amount);

        wallet.setBalance(balanceAfter);
        walletRepository.save(wallet);

        return createTransaction(wallet,TransactionType.CREDIT,transactionSource,amount,balanceBefore,balanceAfter,description);
    }


    // create the transaction history
    private WalletTransaction createTransaction(Wallet wallet,
                                                TransactionType transactionType,
                                                TransactionSource transactionSource,
                                                BigDecimal amount,
                                                BigDecimal balanceBefore,
                                                BigDecimal balanceAfter,
                                                String description) {


        WalletTransaction walletTransaction = new WalletTransaction();

        walletTransaction.setWallet(wallet);
        walletTransaction.setTransactionType(transactionType);
        walletTransaction.setTransactionSource(transactionSource);
        walletTransaction.setAmount(amount);
        walletTransaction.setBalanceBefore(balanceBefore);
        walletTransaction.setBalanceAfter(balanceAfter);
        walletTransaction.setDescription(description);

        walletTransaction.setTransactionStatus(TransactionStatus.COMPLETED);
        return walletTransactionRepository.save(walletTransaction);
    }

    // Load the transaction history
    public List<WalletTransaction> getTransactionHistory(Long userId) {

        // load user wallet
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(()-> new BadRequestException("User Wallet not found"));

        // return transaction history for user
        return walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());
    }

    @Transactional
    public Wallet freezeWallet(Long userId) {

       Wallet wallet = walletRepository.findByUserId(userId)
               .orElseThrow(()-> new BadRequestException("User Wallet not found"));

       if(wallet.getWalletStatus() == WalletStatus.FROZEN) {
            throw new BadRequestException("Wallet is already FROZEN");
       }

        wallet.setWalletStatus(WalletStatus.FROZEN);
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet unfreezeWallet(Long userId) {

        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(()-> new BadRequestException("User Wallet not found"));

        if(wallet.getWalletStatus() == WalletStatus.ACTIVE) {
            throw new BadRequestException("Wallet is already active");
        }

        wallet.setWalletStatus(WalletStatus.ACTIVE);
        return walletRepository.save(wallet);
    }

}
