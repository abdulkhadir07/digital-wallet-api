package com.abdulkhadirjallow.spring_auth_system.controller;

import com.abdulkhadirjallow.spring_auth_system.dto.LoginRequest;
import com.abdulkhadirjallow.spring_auth_system.dto.LoginResponse;
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
        RegisterResponse registerResponse = new RegisterResponse("User registered successfully. Please verify your email.");

        // Return the response with 201 status
        return new ResponseEntity<>(registerResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {

        // call service to do the logic
        authService.login(loginRequest);

        // create response object
        LoginResponse loginResponse = new LoginResponse("login successfully.");

        // return response
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }
}
