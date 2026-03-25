package com.abdulkhadirjallow.spring_auth_system.controller;

import com.abdulkhadirjallow.spring_auth_system.dto.RegisterRequest;
import com.abdulkhadirjallow.spring_auth_system.dto.RegisterResponse;
import com.abdulkhadirjallow.spring_auth_system.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
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
        RegisterResponse registerResponse = new RegisterResponse("User registered successfully. Please verify your email.");

        // Return the response with 201 status
        return new ResponseEntity<>(registerResponse, HttpStatus.CREATED);
    }
}
