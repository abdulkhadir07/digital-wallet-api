package com.abdulkhadirjallow.digitalwalletapi.controller;

import com.abdulkhadirjallow.digitalwalletapi.dto.WalletResponse;
import com.abdulkhadirjallow.digitalwalletapi.dto.WalletTransactionResponse;
import com.abdulkhadirjallow.digitalwalletapi.entity.Wallet;
import com.abdulkhadirjallow.digitalwalletapi.entity.WalletTransaction;
import com.abdulkhadirjallow.digitalwalletapi.security.UserPrincipal;
import com.abdulkhadirjallow.digitalwalletapi.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;

    }

    @GetMapping("/me")
    public ResponseEntity<WalletResponse> meWallet(@AuthenticationPrincipal UserPrincipal principal) {

        // get authenticated user id
        Long userId = principal.getUserId();

        // call service to load the user wallet
        Wallet wallet = walletService.getWalletByUserId(userId);

        return new ResponseEntity<>(toWalletResponse(wallet),HttpStatus.OK);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<WalletTransactionResponse>> transactionHistory(@AuthenticationPrincipal UserPrincipal principal) {

        // get authenticated user id
        Long userId = principal.getUserId();

        // call service to apply logic
        List<WalletTransaction> walletTransaction = walletService.getTransactionHistory(userId);

        // Map Response DTO
        List<WalletTransactionResponse> walletTransactionResponse = walletTransaction.stream()
                .map( tx -> new WalletTransactionResponse (
                        tx.getReference(),
                        tx.getDescription(),
                        tx.getTransactionType(),
                        tx.getTransactionStatus(),
                        tx.getTransactionSource(),
                        tx.getAmount(),
                        tx.getBalanceBefore(),
                        tx.getBalanceAfter(),
                        tx.getCreatedAt()

                ))
                .toList();
        return new ResponseEntity<>(walletTransactionResponse,HttpStatus.OK);
    }

    @PatchMapping("/freeze")
    public ResponseEntity<WalletResponse> freezeWallet(@AuthenticationPrincipal UserPrincipal principal) {

        // get authenticated user id
        Long userId = principal.getUserId();

        // call service to apply logic
        Wallet wallet = walletService.freezeWallet(userId);

        return new ResponseEntity<>(toWalletResponse(wallet),HttpStatus.OK);
    }

    @PatchMapping("/unfreeze")
    public ResponseEntity<WalletResponse> unfreezeWallet(@AuthenticationPrincipal UserPrincipal principal) {

        //get authenticated user id
        Long userId = principal.getUserId();

        // call service to apply logic
        Wallet wallet = walletService.unfreezeWallet(userId);

        return new ResponseEntity<>(toWalletResponse(wallet),HttpStatus.OK);
    }

    // Helper method
    private WalletResponse toWalletResponse(Wallet wallet) {
        return new WalletResponse(
                wallet.getCurrency(),
                wallet.getBalance(),
                wallet.getWalletStatus(),
                wallet.getCreatedAt(),
                wallet.getUpdatedAt()
        );
    }
}




