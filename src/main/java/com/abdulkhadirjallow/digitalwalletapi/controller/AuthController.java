package com.abdulkhadirjallow.digitalwalletapi.controller;

import com.abdulkhadirjallow.digitalwalletapi.dto.*;
import com.abdulkhadirjallow.digitalwalletapi.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest reg) {

        // Call service to do the logic
        authService.register(reg);

        // Create the response object
        RegisterResponse registerResponse = new RegisterResponse("User registered successfully. Please verify your phone number.");

        // Return the response with 201 status
        return new ResponseEntity<>(registerResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {

        // call service to do the logic
        String token = authService.login(loginRequest);

        // create response object
        LoginResponse loginResponse = new LoginResponse( "Login successful", token);

        // return response
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyResponse> verify(@Valid @RequestBody VerifyRequest verifyRequest) {

        // call service to do the logic
        authService.verify(verifyRequest);

        // create response object
        VerifyResponse verifyResponse = new VerifyResponse("Account successfully verified.");

        // return response
        return new ResponseEntity<>(verifyResponse, HttpStatus.OK);
    }
}
