package com.abdulkhadirjallow.digitalwalletapi.security;

import com.abdulkhadirjallow.digitalwalletapi.entity.User;
import com.abdulkhadirjallow.digitalwalletapi.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNumber(phoneNumber.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password."));
        return UserPrincipal.fromUser(user);
    }
}