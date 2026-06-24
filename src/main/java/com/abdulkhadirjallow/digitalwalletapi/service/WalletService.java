package com.abdulkhadirjallow.digitalwalletapi.service;

import com.abdulkhadirjallow.digitalwalletapi.dto.DepositRequest;
import com.abdulkhadirjallow.digitalwalletapi.dto.DepositResponse;
import com.abdulkhadirjallow.digitalwalletapi.dto.WithdrawalRequest;
import com.abdulkhadirjallow.digitalwalletapi.dto.WithdrawalResponse;
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
    public WalletTransaction debitWallet(Long userId,BigDecimal amount,TransactionSource transactionSource, String description, String transferReference) {

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

        return createTransaction(wallet,TransactionType.DEBIT,transactionSource,amount,balanceBefore,balanceAfter,description,transferReference);
    }

    @Transactional
    public WalletTransaction creditWallet(Long userId, BigDecimal amount, TransactionSource transactionSource,String description, String transferReference) {

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

        return createTransaction(wallet,TransactionType.CREDIT,transactionSource,amount,balanceBefore,balanceAfter,description,transferReference);
    }


    // create the transaction history
    private WalletTransaction createTransaction(Wallet wallet,
                                                TransactionType transactionType,
                                                TransactionSource transactionSource,
                                                BigDecimal amount,
                                                BigDecimal balanceBefore,
                                                BigDecimal balanceAfter,
                                                String description,
                                                String transferReference) {


        WalletTransaction walletTransaction = new WalletTransaction();

        walletTransaction.setWallet(wallet);
        walletTransaction.setTransactionType(transactionType);
        walletTransaction.setTransactionSource(transactionSource);
        walletTransaction.setAmount(amount);
        walletTransaction.setBalanceBefore(balanceBefore);
        walletTransaction.setBalanceAfter(balanceAfter);
        walletTransaction.setDescription(description);
        walletTransaction.setTransferReference(transferReference);

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

    @Transactional
    public DepositResponse deposit(Long userId, DepositRequest depositRequest) {

        // Find the user wallet
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("User wallet not found"));

        // TODO: integrate payment processor (Stripe for card, Plaid for bank, agent system for AGENT)
        // For now we credit the wallet directly as a stub

        String description = "Deposit via " + depositRequest.getPaymentMethod().name().replace("_", " ").toLowerCase();

        WalletTransaction transaction = creditWallet(
                userId,
                depositRequest.getAmount(),
                TransactionSource.BANK_DEPOSIT,
                description,
                null
        );

        // Reload wallet to get updated balance
        Wallet updatedWallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("User wallet not found"));

        return new DepositResponse(
                "Deposit successful",
                depositRequest.getAmount(),
                updatedWallet.getBalance(),
                updatedWallet.getCurrency(),
                depositRequest.getPaymentMethod(),
                transaction.getReference(),
                transaction.getCreatedAt()
        );
    }

    @Transactional
    public WithdrawalResponse withdraw(Long userId, WithdrawalRequest withdrawalRequest) {

        // TODO: integrate payout processor (Stripe for card, ACH/Plaid for bank, agent system for AGENT)
        // For now we debit the wallet directly as a stub

        String description = "Withdrawal via " + withdrawalRequest.getPaymentMethod().name().replace("_", " ").toLowerCase();

        WalletTransaction transaction = debitWallet(
                userId,
                withdrawalRequest.getAmount(),
                TransactionSource.BANK_WITHDRAWAL,
                description,
                null
        );

        Wallet updatedWallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("User wallet not found"));

        return new WithdrawalResponse(
                "Withdrawal request submitted successfully",
                withdrawalRequest.getAmount(),
                updatedWallet.getBalance(),
                updatedWallet.getCurrency(),
                withdrawalRequest.getPaymentMethod(),
                transaction.getReference(),
                transaction.getCreatedAt()
        );
    }

}
