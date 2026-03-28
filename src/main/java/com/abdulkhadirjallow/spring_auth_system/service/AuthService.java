package com.abdulkhadirjallow.spring_auth_system.service;

import com.abdulkhadirjallow.spring_auth_system.dto.LoginRequest;
import com.abdulkhadirjallow.spring_auth_system.dto.RegisterRequest;
import com.abdulkhadirjallow.spring_auth_system.dto.VerifyRequest;
import com.abdulkhadirjallow.spring_auth_system.entity.User;
import com.abdulkhadirjallow.spring_auth_system.exception.BadRequestException;
import com.abdulkhadirjallow.spring_auth_system.exception.UnauthorizedException;
import com.abdulkhadirjallow.spring_auth_system.repository.UserRepository;
import com.abdulkhadirjallow.spring_auth_system.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void register(RegisterRequest registerRequest) {

        // Email Uniqueness (checks if email exist)
        if (userRepository.existsByEmail(registerRequest.getEmail().trim().toLowerCase())) {
            throw new BadRequestException("Email already exists");
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

    public String login(LoginRequest loginRequest) {

        // find user by email
       User user = userRepository.findByEmail(loginRequest.getEmail().trim().toLowerCase())
               .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

       // Compare raw password with the hashed password in DB
       if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
           throw new UnauthorizedException("Invalid email or password");
       }

       // check if account is verified
       if (!user.isVerified()) {
           throw new UnauthorizedException("Please verify your account before logging in");
       }

       // return the generated token string
       return jwtService.generateToken(user);
    }

    public void verify(VerifyRequest verifyRequest) {

        // find user
        User user = userRepository.findByEmail(verifyRequest.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new BadRequestException("User not found"));

        // Check if user is already verified
        if (user.isVerified()) {
            throw new BadRequestException("Account is already verified");
        }

        // check if verification code exist
        if(user.getVerificationCode() == null || user.getVerificationCodeExpiresAt() == null ){
            throw new RuntimeException("No verification code found");
        }

        // check if verification code expires
        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Verification code has expired");
        }

        // check if verification matches
        if(!user.getVerificationCode().equals(verifyRequest.getVerificationCode().trim())) {
            throw new BadRequestException("Verification code does not match");
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
