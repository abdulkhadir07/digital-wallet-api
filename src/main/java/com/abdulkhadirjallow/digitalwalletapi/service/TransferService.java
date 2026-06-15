package com.abdulkhadirjallow.digitalwalletapi.service;

import com.abdulkhadirjallow.digitalwalletapi.dto.*;
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
import java.math.RoundingMode;
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

        // calculate transfer fee, FX rate, recipient amount, and total debit
        TransferCalculation calculation = calculateTransfer(
                transferRequest.getSenderAmount(),
                senderWallet.getCurrency(),
                recipientWallet.getCurrency(),
                senderUser.getCountry(),
                recipient.getCountry()
        );

        // Check KYC status only if senderAmount is greater than or equal to 200
        BigDecimal limit = new BigDecimal("200.00");
        if (transferRequest.getSenderAmount().compareTo(limit) >= 0) {
            KycProfile kycProfile = kycProfileRepository.findByUserId(senderUser.getId())
                    .orElseThrow(() -> new BadRequestException("KYC verification is required for transfers $200 and above"));

            validateKycRequirements(kycProfile);
        }
        // check if sender balance is enough to cover sendAmount + fee
        if (senderWallet.getBalance().compareTo(calculation.totalDebitAmount) < 0) {
            throw new BadRequestException("You have insufficient funds");
        }

        // debit sender and credit recipient
        walletService.debitWallet(
                senderUser.getId(),
                calculation.totalDebitAmount,
                TransactionSource.TRANSFER,
                transferRequest.getDescription()
        );

        walletService.creditWallet(
                recipient.getId(),
                calculation.recipientAmount,
                TransactionSource.TRANSFER,
                transferRequest.getDescription()
        );

        // Map request DTO to entity
        Transfer transfer = new Transfer();

        transfer.setSenderUser(senderUser);
        transfer.setRecipientUser(recipient);
        transfer.setSenderWallet(senderWallet);
        transfer.setRecipientWallet(recipientWallet);
        transfer.setSenderCurrency(senderWallet.getCurrency());
        transfer.setRecipientCurrency(recipientWallet.getCurrency());
        transfer.setSenderAmount(transferRequest.getSenderAmount());
        transfer.setRecipientAmount(calculation.recipientAmount);
        transfer.setFee(calculation.fee);
        transfer.setExchangeRate(calculation.exchangeRate);
        transfer.setDescription(transferRequest.getDescription());
        transfer.setTransferStatus(TransferStatus.COMPLETED);

        return transferRepository.save(transfer);
    }

    public TransferQuoteResponse quoteTransfer(Long userId, TransferQuoteRequest transferQuoteRequest) {

        User senderUser = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        User recipient = findRecipient(transferQuoteRequest.getRecipientPhoneNumber());

        if (senderUser.getId().equals(recipient.getId())) {
            throw new BadRequestException("You are not allowed to transfer yourself");
        }

        Wallet senderWallet = walletRepository.findByUserId(senderUser.getId())
                .orElseThrow(() -> new BadRequestException("Sender wallet not found"));

        Wallet recipientWallet = walletRepository.findByUserId(recipient.getId())
                .orElseThrow(() -> new BadRequestException("Recipient wallet not found"));

        TransferCalculation calculation = calculateTransfer(
                transferQuoteRequest.getSenderAmount(),
                senderWallet.getCurrency(),
                recipientWallet.getCurrency(),
                senderUser.getCountry(),
                recipient.getCountry()
        );

        return new TransferQuoteResponse(
                recipient.getPhoneNumber(),
                RecipientInfo.from(recipient),
                transferQuoteRequest.getSenderAmount(),
                calculation.recipientAmount,
                calculation.fee,
                calculation.totalDebitAmount,
                calculation.exchangeRate,
                calculation.retailRate,
                calculation.fxMarkupPercent,
                senderWallet.getCurrency(),
                recipientWallet.getCurrency(),
                calculation.transferType,
                "Transfer quote generated successfully"
        );
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

    public List<RecipientSearchResponse> searchRecipients(Long userId, String phoneNumber) {
        String searchTerm = phoneNumber == null ? "" : phoneNumber.trim();

        if (searchTerm.length() < 7) {
            return List.of();
        }

        return userRepository
                .findTop5ByPhoneNumberContainingAndVerifiedTrueAndIdNot(searchTerm, userId)
                .stream()
                .map(RecipientSearchResponse::from)
                .toList();
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

    private static class TransferCalculation {
        private final BigDecimal fee;
        private final BigDecimal exchangeRate;
        private final BigDecimal retailRate;
        private final BigDecimal recipientAmount;
        private final BigDecimal totalDebitAmount;
        private final BigDecimal fxMarkupPercent;
        private final TransferType transferType;

        private TransferCalculation(
                BigDecimal fee,
                BigDecimal exchangeRate,
                BigDecimal retailRate,
                BigDecimal recipientAmount,
                BigDecimal totalDebitAmount,
                BigDecimal fxMarkupPercent,
                TransferType transferType
        ) {
            this.fee = fee;
            this.exchangeRate = exchangeRate;
            this.retailRate = retailRate;
            this.recipientAmount = recipientAmount;
            this.totalDebitAmount = totalDebitAmount;
            this.fxMarkupPercent = fxMarkupPercent;
            this.transferType = transferType;
        }
    }

    private TransferCalculation calculateTransfer(
            BigDecimal senderAmount,
            Currency senderCurrency,
            Currency recipientCurrency,
            Country senderCountry,
            Country recipientCountry
    ) {
        BigDecimal exchangeRate = BigDecimal.ONE;
        BigDecimal retailRate = BigDecimal.ONE;
        BigDecimal recipientAmount = senderAmount;
        BigDecimal fee = BigDecimal.ZERO;
        BigDecimal fxMarkupPercent = BigDecimal.ZERO;
        TransferType transferType;

        if (senderCountry == recipientCountry) {
            transferType = TransferType.DOMESTIC_FREE;
        } else if (senderCurrency == recipientCurrency) {
            transferType = TransferType.INTERNATIONAL_SAME_CURRENCY;
            fee = senderAmount.multiply(new BigDecimal("0.01"));
        } else {
            transferType = TransferType.INTERNATIONAL_FX;
            fxMarkupPercent = new BigDecimal("2.00");

            exchangeRate = fxRateService.getExchangeRate(senderCurrency, recipientCurrency);
            retailRate = exchangeRate.multiply(new BigDecimal("0.98")).setScale(4, RoundingMode.HALF_DOWN);
            recipientAmount = senderAmount.multiply(retailRate).setScale(2, RoundingMode.HALF_DOWN);
        }

        BigDecimal totalDebitAmount = senderAmount.add(fee);

        return new TransferCalculation(
                fee,
                exchangeRate,
                retailRate,
                recipientAmount,
                totalDebitAmount,
                fxMarkupPercent,
                transferType
        );
    }


}
