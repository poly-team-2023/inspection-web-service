package com.service.inspection.service;

import com.service.inspection.entities.User;
import com.service.inspection.repositories.UserRepository;
import jakarta.persistence.NonUniqueResultException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Value("${login.secret.code}")
    private String systemSecretCode;

    public Authentication auth(String email, String password, String secretCode) {

        if (!Objects.equals(secretCode, systemSecretCode)) {
            throw new BadCredentialsException("Invalid secret code");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    public User createNewUser(User newUser) {
        if (userRepository.existsByEmail(newUser.getEmail())) {
            throw new NonUniqueResultException("Email must be uniq !"); // нормальная обработка ошибок
        }
        return userRepository.save(newUser);
    }
}
