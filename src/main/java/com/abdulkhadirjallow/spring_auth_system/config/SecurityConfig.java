package com.abdulkhadirjallow.spring_auth_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless APIs
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/auth/register").permitAll() // Anyone can register
                            .anyRequest().authenticated() // Everything else is locked down
                    ).sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // We use JWT, not Cookies
                    );

            return http.build();
        }

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
}
