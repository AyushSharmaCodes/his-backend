package com.his.authservice.config;

import com.his.authservice.service.AuthService;
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
    public void run(String... args) throws Exception {
        // Initialize default roles
        authService.initializeRoles();
    }
}