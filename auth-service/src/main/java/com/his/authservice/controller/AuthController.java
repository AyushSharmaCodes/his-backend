package com.his.authservice.controller;

import com.his.authservice.dto.*;
import com.his.authservice.service.AuthService;
import com.his.authservice.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication endpoints
 * Handles user registration, login, and token validation
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Register a new user
     * @param registerRequest user registration details
     * @return success/error message
     */
    @PostMapping("/register")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            MessageResponse response = authService.registerUser(registerRequest);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error occurred during registration: " + e.getMessage(), false));
        }
    }
    
    /**
     * Authenticate user and return JWT token
     * @param loginRequest user login credentials
     * @return JWT token and user details
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(jwtResponse);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Invalid username or password", false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error occurred during authentication: " + e.getMessage(), false));
        }
    }
    
    /**
     * Validate JWT token and return user details
     * @param request HTTP request containing Authorization header
     * @return user validation response
     */
    @GetMapping("/validate")
    public ResponseEntity<UserValidationResponse> validateToken(jakarta.servlet.http.HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            String token = jwtUtil.parseJwt(authHeader);
            
            if (token == null) {
                return ResponseEntity.badRequest()
                        .body(new UserValidationResponse(null, null, false));
            }
            
            UserValidationResponse response = authService.validateToken(token);
            
            if (response.isValid()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UserValidationResponse(null, null, false));
        }
    }
    
    /**
     * Test endpoint for authenticated users
     * @return success message
     */
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> userAccess() {
        return ResponseEntity.ok(new MessageResponse("User Content."));
    }
    
    /**
     * Test endpoint for admin users only
     * @return success message
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> adminAccess() {
        return ResponseEntity.ok(new MessageResponse("Admin Board."));
    }
}