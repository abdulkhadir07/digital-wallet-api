package com.abdulkhadirjallow.spring_auth_system.service;

import com.abdulkhadirjallow.spring_auth_system.dto.RegisterRequest;
import com.abdulkhadirjallow.spring_auth_system.entity.User;
import com.abdulkhadirjallow.spring_auth_system.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        if(userRepository.existsByEmail(registerRequest.getEmail())) {
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

        // Save to database
        userRepository.save(newUser);
    }
}
