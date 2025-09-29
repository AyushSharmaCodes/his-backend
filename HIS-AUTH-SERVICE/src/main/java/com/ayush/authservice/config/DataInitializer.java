package com.ayush.authservice.config;

import com.ayush.authservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Data Initialization Component
 * Initializes default roles when the application starts
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AuthService authService;

    @Override
    public void run(String... args) {
        // Initialize default roles
        authService.initializeRoles();
    }
}