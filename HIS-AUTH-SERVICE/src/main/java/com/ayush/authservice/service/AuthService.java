package com.ayush.authservice.service;


import com.ayush.authservice.entity.Role;
import com.ayush.authservice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for authentication operations
 * Handles user registration, login, and token validation
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ =  {@Autowired})
public class AuthService {

    private final RoleRepository roleRepository;

    /**
     * Initialize default roles if they don't exist
     */
    @Transactional
    public void initializeRoles() {
        if (!roleRepository.existsByName("ROLE_USER")) {
            roleRepository.save(new Role("ROLE_USER"));
        }
        if (!roleRepository.existsByName("ROLE_ADMIN")) {
            roleRepository.save(new Role("ROLE_ADMIN"));
        }
    }
}
