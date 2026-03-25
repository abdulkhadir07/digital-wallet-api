package com.abdulkhadirjallow.spring_auth_system.service;

import com.abdulkhadirjallow.spring_auth_system.dto.RegisterRequest;
import com.abdulkhadirjallow.spring_auth_system.entity.User;
import com.abdulkhadirjallow.spring_auth_system.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(RegisterRequest registerRequest) {

        // Email Uniqueness (checks if email exist)
        if(userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Map DTO to Entity: Create the Entity from DTO
        User newUser = new User();

        newUser.setFirstName(registerRequest.getFirstName());
        newUser.setLastName(registerRequest.getLastName());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setDateOfBirth(registerRequest.getDateOfBirth());
        newUser.setPassword(registerRequest.getPassword());// HASH later
        newUser.setVerified(false);

        // Save to database
        return userRepository.save(newUser);
    }
}
