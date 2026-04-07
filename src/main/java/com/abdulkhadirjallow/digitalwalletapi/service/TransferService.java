package com.abdulkhadirjallow.digitalwalletapi.service;

import com.abdulkhadirjallow.digitalwalletapi.dto.TransferRequest;
import com.abdulkhadirjallow.digitalwalletapi.entity.*;
import com.abdulkhadirjallow.digitalwalletapi.enums.*;
import com.abdulkhadirjallow.digitalwalletapi.exception.BadRequestException;
import com.abdulkhadirjallow.digitalwalletapi.repository.KycProfileRepository;
import com.abdulkhadirjallow.digitalwalletapi.repository.TransferRepository;
import com.abdulkhadirjallow.digitalwalletapi.repository.UserRepository;
import com.abdulkhadirjallow.digitalwalletapi.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final UserRepository userRepository;
    private final KycProfileRepository kycProfileRepository;
    private final WalletRepository walletRepository;
    private final WalletService walletService;
    private final FxRateService fxRateService;

    public TransferService(TransferRepository transferRepository,
                           UserRepository userRepository,
                           KycProfileRepository kycProfileRepository,
                           WalletRepository walletRepository,
                           WalletService walletService, FxRateService fxRateService) {
        this.transferRepository = transferRepository;
        this.userRepository = userRepository;
        this.kycProfileRepository = kycProfileRepository;
        this.walletRepository = walletRepository;
        this.walletService = walletService;
        this.fxRateService = fxRateService;
    }

    // create transfer
    @Transactional
    public Transfer transfer(Long userId, TransferRequest transferRequest) {

        // find user(sender)
        User senderUser = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        // find user(recipient)
        User recipient = findRecipient(transferRequest.getRecipientPhoneNumber());

        // self-transfer check
        if (senderUser.getId().equals(recipient.getId())) {
            throw new BadRequestException("You are not allowed to transfer yourself");
        }

        // find the senderWallet
        Wallet senderWallet = walletRepository.findByUserId(senderUser.getId())
                .orElseThrow(() -> new BadRequestException("Sender wallet not found"));

        // find the receiverWallet
        Wallet recipientWallet = walletRepository.findByUserId(recipient.getId())
                .orElseThrow(() -> new BadRequestException("Recipient wallet not found"));

        // calculate the total fees
        BigDecimal totalFees = calculateFee(
                transferRequest.getSenderAmount(),
                senderWallet.getCurrency(),
                recipientWallet.getCurrency(),
                senderUser.getCountry(),
                recipient.getCountry()
        );

        // check if sender balance is enough to cover sendAmount + fee
        if (senderWallet.getBalance().compareTo(transferRequest.getSenderAmount().add(totalFees)) < 0) {
            throw new BadRequestException("You have insufficient funds");
        }

        // Check KYC status only if senderAmount is greater than or equal to 200
        BigDecimal limit = new BigDecimal("200.00");
        if (transferRequest.getSenderAmount().compareTo(limit) >= 0) {
            KycProfile kycProfile = kycProfileRepository.findByUserId(senderUser.getId())
                    .orElseThrow(() -> new BadRequestException("KYC verified is required for transfers $200 and above"));

            validateKycRequirements(kycProfile);
        }

        // calculate recipientAmount
        BigDecimal recipientAmount;
        BigDecimal exchangeRate = fxRateService.getExchangeRate(senderWallet.getCurrency(), recipientWallet.getCurrency());

        if(senderUser.getCountry().equals(recipient.getCountry()) || senderWallet.getCurrency().equals(recipientWallet.getCurrency())) {
            recipientAmount = transferRequest.getSenderAmount();
        } else  {
             recipientAmount = transferRequest.getSenderAmount().multiply(exchangeRate);
        }

        // debit senderUser and credit recipient accordingly
        BigDecimal totalDebitAmount = transferRequest.getSenderAmount().add(totalFees);
        walletService.debitWallet(senderUser.getId(),totalDebitAmount, TransactionSource.TRANSFER,transferRequest.getDescription());
        walletService.creditWallet(recipient.getId(), recipientAmount, TransactionSource.TRANSFER,transferRequest.getDescription());

        // Map request DTO to entity
        Transfer transfer = new Transfer();

        transfer.setSenderUser(senderUser);
        transfer.setRecipientUser(recipient);
        transfer.setSenderWallet(senderWallet);
        transfer.setRecipientWallet(recipientWallet);
        transfer.setSenderCurrency(senderWallet.getCurrency());
        transfer.setRecipientCurrency(recipientWallet.getCurrency());
        transfer.setSenderAmount(transferRequest.getSenderAmount());
        transfer.setRecipientAmount(recipientAmount);
        transfer.setFee(totalFees);
        transfer.setExchangeRate(exchangeRate);
        transfer.setDescription(transferRequest.getDescription());
        transfer.setTransferStatus(TransferStatus.COMPLETED);

        return transferRepository.save(transfer);
    }

    public List<Transfer> transferHistory(Long userId) {
        // get all transfers for a specific user
        return transferRepository.findAllByUserId(userId);
    }

    public Transfer getTransferByReference(String reference, Long userId) {

        // find transfer by reference
        Transfer transfer = transferRepository.findByReference(reference)
                .orElseThrow(() -> new BadRequestException("Reference not found"));


        // ownership check
        if (!transfer.getSenderUser().getId().equals(userId) && !transfer.getRecipientUser().getId().equals(userId)) {
            throw new BadRequestException("You cannot transfer money to yourself");
        }
        // return transfer by reference
       return transfer;
    }

    public List<Transfer> getTransferSent(Long senderUserId) {
        // return all transfers sent by user
        return transferRepository.findBySenderUserIdOrderByCreatedAtDesc(senderUserId);
    }

    public List<Transfer> getTransferReceived(Long recipientUserId) {

        // return all transfers received by user
        return transferRepository.findByRecipientUserIdOrderByCreatedAtDesc(recipientUserId);
    }

    // helper methods
    // Recipient lookup
    private User findRecipient(String phoneNumber) {

        // find user(recipient)
       return userRepository.findByPhoneNumber(phoneNumber.trim())
                .orElseThrow(() -> new BadRequestException("Recipient not found"));
    }

    private void validateKycRequirements(KycProfile kycProfile) {

        // check if user is Kyc verified
        if (kycProfile.getStatus() != KycStatus.VERIFIED ) {
            throw new BadRequestException("Please verify your identity to send amounts of $200 and above");
        }
    }

    private BigDecimal calculateFee(BigDecimal senderAmount, Currency senderCurrency, Currency recipientCurrency, Country senderCountry, Country recipientCountry) {

        // Domestic transfer fees
        if(senderCountry == recipientCountry) {
            return BigDecimal.ZERO;
        }

        // Same currency but different countries transfer fees
        if(senderCurrency == recipientCurrency) {
            return senderAmount.multiply(new BigDecimal("0.01"));
        }

        // All international transfers (different currencies) transfer fees
        return senderAmount.multiply(new BigDecimal("0.02"));
    }
}
