package com.abdulkhadirjallow.digitalwalletapi.repository;

import com.abdulkhadirjallow.digitalwalletapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // fetching a specific user for login
    Optional<User> findByPhoneNumber(String phoneNumber);

    // Checks if email already exists
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
