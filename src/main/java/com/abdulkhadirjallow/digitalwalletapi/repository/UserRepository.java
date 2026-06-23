package com.abdulkhadirjallow.digitalwalletapi.repository;

import com.abdulkhadirjallow.digitalwalletapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // fetching a specific user for login
    Optional<User> findByPhoneNumber(String phoneNumber);
    @Query("select u from User u where replace(u.phoneNumber, ' ', '') = :phoneNumber")
    Optional<User> findByNormalizedPhoneNumber(@Param("phoneNumber") String phoneNumber);
    List<User> findTop5ByPhoneNumberContainingAndVerifiedTrueAndIdNot(String phoneNumber, Long userId);

    // Checks if email already exists
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
