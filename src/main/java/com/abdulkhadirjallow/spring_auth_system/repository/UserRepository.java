package com.abdulkhadirjallow.spring_auth_system.repository;

import com.abdulkhadirjallow.spring_auth_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // fetching a specific user for login
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);

    // Checks if email already exists
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
