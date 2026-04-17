package com.sky.tenant.service;

import com.sky.tenant.entity.User;
import com.sky.tenant.repository.UserRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User syncUserWithKeycloak(Jwt jwt) {
        // Extract Keycloak strictly assigned Subject UUID
        UUID keycloakId = UUID.fromString(jwt.getSubject());
        
        return userRepository.findByKeycloakId(keycloakId)
            .map(existingUser -> {
                // If they change their name/email in Keycloak or Google, we capture it instantly on login
                existingUser.setEmail(jwt.getClaimAsString("email"));
                existingUser.setFirstName(jwt.getClaimAsString("given_name"));
                existingUser.setLastName(jwt.getClaimAsString("family_name"));
                existingUser.setUsername(jwt.getClaimAsString("preferred_username"));
                return userRepository.save(existingUser);
            })
            .orElseGet(() -> {
                // Completely new user to the Spring Boot App
                // We create a skeleton user matching the Keycloak claims
                User newUser = new User();
                newUser.setKeycloakId(keycloakId);
                newUser.setEmail(jwt.getClaimAsString("email"));
                newUser.setFirstName(jwt.getClaimAsString("given_name"));
                newUser.setLastName(jwt.getClaimAsString("family_name"));
                newUser.setUsername(jwt.getClaimAsString("preferred_username"));
                newUser.setIsActive(true);
                return userRepository.save(newUser);
            });
    }

    @Transactional(readOnly = true)
    public User getCurrentUser(Jwt jwt) {
        UUID keycloakId = UUID.fromString(jwt.getSubject());
        return userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found for Keycloak ID: " + keycloakId));
    }

    @Transactional(readOnly = true)
    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }
}
