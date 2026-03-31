package com.abdulkhadirjallow.spring_auth_system.controller;

import com.abdulkhadirjallow.spring_auth_system.dto.KycResponse;
import com.abdulkhadirjallow.spring_auth_system.dto.KycSubmitRequest;
import com.abdulkhadirjallow.spring_auth_system.entity.KycProfile;
import com.abdulkhadirjallow.spring_auth_system.security.UserPrincipal;
import com.abdulkhadirjallow.spring_auth_system.service.KycService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kyc")
public class KycController {

    private final KycService kycService;

    public KycController(KycService kycService) {
        this.kycService = kycService;
    }

    @PostMapping("/submit")
    public ResponseEntity<KycResponse> submitKyc(
            @RequestBody @Valid KycSubmitRequest kycSubmitRequest,
            @AuthenticationPrincipal UserPrincipal principal
    ) {

        // Get real User entity from the wrapper
        Long userId = principal.getUserId();

        // call servic to do the logic
        KycProfile savedKyc = kycService.submitKyc(userId, kycSubmitRequest);

        // create the response entity
        KycResponse kycResponse = new KycResponse(
                "KYC submitted and under review",
                savedKyc.getStatus(),
                savedKyc.getSubmittedAt());

      return new ResponseEntity<>(kycResponse, HttpStatus.CREATED);
    }
}
