package com.abdulkhadirjallow.spring_auth_system.service;

import com.abdulkhadirjallow.spring_auth_system.dto.LoginRequest;
import com.abdulkhadirjallow.spring_auth_system.dto.RegisterRequest;
import com.abdulkhadirjallow.spring_auth_system.dto.VerifyRequest;
import com.abdulkhadirjallow.spring_auth_system.entity.User;
import com.abdulkhadirjallow.spring_auth_system.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest registerRequest) {

        // Email Uniqueness (checks if email exist)
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Map DTO to Entity: Create the Entity from DTO
        User newUser = new User();

        newUser.setFirstName(registerRequest.getFirstName().trim());
        newUser.setLastName(registerRequest.getLastName().trim());
        newUser.setEmail(registerRequest.getEmail().trim().toLowerCase());
        newUser.setDateOfBirth(registerRequest.getDateOfBirth());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));// HASH later
        newUser.setVerified(false);

        String code = generateVerificationCode();
        newUser.setVerificationCode(code);
        newUser.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));

        // Save to database
        userRepository.save(newUser);

        System.out.println("Verification code for " + newUser.getEmail() + ": " + code);
    }

    public void login(LoginRequest loginRequest) {

        // find user by email
       User user = userRepository.findByEmail(loginRequest.getEmail())
               .orElseThrow(() -> new RuntimeException("Invalid email or password"));

       // Compare raw password with the hashed password in DB
       if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
           throw new RuntimeException("Invalid email or password");
       }

       // check if account is verified
       if (!user.isVerified()) {
           throw new RuntimeException("Please verify your email before logging in");
       }
    }

    public void verify(VerifyRequest verifyRequest) {

        // find user
        User user = userRepository.findByEmail(verifyRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is already verified
        if (user.isVerified()) {
            throw new RuntimeException("Account is already verified");
        }

        // check if verification code exist
        if(user.getVerificationCode() == null || user.getVerificationCodeExpiresAt() == null ){
            throw new RuntimeException("No verification code found");
        }

        // check if verification code expires
        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification code has expired");
        }

        // check if verification has expired
        if(!user.getVerificationCode().equals(verifyRequest.getVerificationCode())) {
            throw new RuntimeException("Verification code does not match");
        }

        // update user state, clear and save
        user.setVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        userRepository.save(user);
    }

    private String generateVerificationCode() {
       Random random = new Random();
       int code = 100000 + random.nextInt(900000);
       return String.valueOf(code);
    }
}
