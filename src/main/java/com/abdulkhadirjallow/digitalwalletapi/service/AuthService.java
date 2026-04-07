package com.abdulkhadirjallow.digitalwalletapi.service;

import com.abdulkhadirjallow.digitalwalletapi.dto.LoginRequest;
import com.abdulkhadirjallow.digitalwalletapi.dto.RegisterRequest;
import com.abdulkhadirjallow.digitalwalletapi.dto.VerifyRequest;
import com.abdulkhadirjallow.digitalwalletapi.entity.User;
import com.abdulkhadirjallow.digitalwalletapi.exception.BadRequestException;
import com.abdulkhadirjallow.digitalwalletapi.exception.UnauthorizedException;
import com.abdulkhadirjallow.digitalwalletapi.repository.UserRepository;
import com.abdulkhadirjallow.digitalwalletapi.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final WalletService walletService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, WalletService walletService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.walletService = walletService;
    }

    @Transactional
    public void register(RegisterRequest registerRequest) {

        // Phone Number Uniqueness (check if phone number already exist)
        if (userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber().trim())) {
            throw new BadRequestException("Phone number already exists");
        }

        // Email Uniqueness (checks if email exist)
        if (userRepository.existsByEmail(registerRequest.getEmail().trim().toLowerCase())) {
            throw new BadRequestException("Email already exists");
        }

        // Map DTO to Entity: Create the Entity from DTO
        User newUser = new User();

        newUser.setFirstName(registerRequest.getFirstName().trim());
        newUser.setLastName(registerRequest.getLastName().trim());
        newUser.setDateOfBirth(registerRequest.getDateOfBirth());
        newUser.setCountry(registerRequest.getCountry());
        newUser.setPhoneNumber(registerRequest.getPhoneNumber().trim());
        newUser.setEmail(registerRequest.getEmail().trim().toLowerCase());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));// HASH later
        newUser.setVerified(false);

        String code = generateVerificationCode();
        newUser.setVerificationCode(code);
        newUser.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(10));

        // Save to database
        User savedUser = userRepository.save(newUser);

        // Auto-create wallet o=
        walletService.createWallet(savedUser.getId());

        System.out.println("Verification code for " + newUser.getPhoneNumber() + ": " + code);
    }

    public String login(LoginRequest loginRequest) {

        // find user by phone number
       User user = userRepository.findByPhoneNumber(loginRequest.getPhoneNumber().trim())
               .orElseThrow(() -> new UnauthorizedException("Invalid phone number or password"));

       // Compare raw password with the hashed password in DB
       if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
           throw new UnauthorizedException("Invalid phone number or password");
       }

       // check if account is verified
       if (!user.isVerified()) {
           throw new UnauthorizedException("Please verify your account before logging in");
       }

       // return the generated token string
       return jwtService.generateToken(user);
    }

    public void verify(VerifyRequest verifyRequest) {

        // find user by phone Number
        User user = userRepository.findByPhoneNumber(verifyRequest.getPhoneNumber().trim())
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
        if(!user.getVerificationCode().trim().equals(verifyRequest.getVerificationCode().trim())) {
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
