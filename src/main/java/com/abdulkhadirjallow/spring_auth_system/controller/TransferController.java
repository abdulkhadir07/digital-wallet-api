package com.abdulkhadirjallow.spring_auth_system.controller;

import com.abdulkhadirjallow.spring_auth_system.dto.RecipientInfo;
import com.abdulkhadirjallow.spring_auth_system.dto.SenderInfo;
import com.abdulkhadirjallow.spring_auth_system.dto.TransferRequest;
import com.abdulkhadirjallow.spring_auth_system.dto.TransferResponse;
import com.abdulkhadirjallow.spring_auth_system.entity.Transfer;
import com.abdulkhadirjallow.spring_auth_system.security.UserPrincipal;
import com.abdulkhadirjallow.spring_auth_system.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transfer")
public class TransferController {
    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/send")
    public ResponseEntity <TransferResponse> send(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody TransferRequest transferRequest) {

        // get authenticated user
        Long userId = principal.getUserId();

        // call service to apply logic
        Transfer transfer = transferService.transfer(userId, transferRequest);

        TransferResponse transferResponse = toTransferResponse(transfer);
        transferResponse.setMessage("Your transfer has been sent successfully");

        // return response
        return new ResponseEntity<>(transferResponse,HttpStatus.CREATED);
    }

    @GetMapping("/history")
    public ResponseEntity <List<TransferResponse>> transferHistory(@AuthenticationPrincipal UserPrincipal principal) {

        // get authenticated user
        Long userId = principal.getUserId();

        // call service to apply logic
        List<Transfer> transfers = transferService.transferHistory(userId);

        // Map to list
        List<TransferResponse> response = transfers.stream()
                .map(this::toTransferResponse)
                .toList();

        // return response list
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{reference}")
    public ResponseEntity <TransferResponse> getTransferByReference(@AuthenticationPrincipal UserPrincipal principal,@PathVariable String reference) {

        // call service to apply logic
        Transfer transfer = transferService.getTransferByReference(reference, principal.getUserId());

        // return response
        return new ResponseEntity<>(toTransferResponse(transfer),HttpStatus.OK);
    }

    @GetMapping("/sent")
    public ResponseEntity <List<TransferResponse>> getTransferSent(@AuthenticationPrincipal UserPrincipal principal) {

        // get authenticated user
        Long senderUserId = principal.getUserId();

        // call service to apply logic
        List<Transfer> transfer = transferService.getTransferSent(senderUserId);

        // map to list
        List<TransferResponse> response = transfer.stream()
                .map(this::toTransferResponse)
                .toList();

        // return response list
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/received")
    public ResponseEntity <List<TransferResponse>> getTransferReceived(@AuthenticationPrincipal UserPrincipal principal) {

        // get authenticated user
        Long recipientUserId = principal.getUserId();

        // call service to apply logic
        List<Transfer> transfer = transferService.getTransferReceived(recipientUserId);

        // map to list
        List <TransferResponse> response = transfer.stream()
                .map(this::toTransferResponse)
                .toList();

        // return response list
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    // private helper
    private TransferResponse toTransferResponse(Transfer transfer) {
        return new TransferResponse(
                transfer.getReference(),
                transfer.getTransferStatus(),
                null,
                transfer.getDescription(),
                SenderInfo.from(transfer.getSenderUser()),
                transfer.getSenderAmount(),
                transfer.getRecipientAmount(),
                transfer.getFee(),
                transfer.getSenderCurrency(),
                transfer.getRecipientCurrency(),
                RecipientInfo.from(transfer.getRecipientUser()),
                transfer.getCreatedAt()
        );
    }

}
