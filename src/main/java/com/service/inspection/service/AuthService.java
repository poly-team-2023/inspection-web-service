package com.service.inspection.service;

import com.service.inspection.entities.User;
import com.service.inspection.repositories.UserRepository;
import jakarta.persistence.NonUniqueResultException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public Authentication auth(String email, String password) {
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
