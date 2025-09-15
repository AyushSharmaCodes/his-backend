package com.his.authservice.service;

import com.his.authservice.dto.*;
import com.his.authservice.entity.Role;
import com.his.authservice.entity.User;
import com.his.authservice.repository.RoleRepository;
import com.his.authservice.repository.UserRepository;
import com.his.authservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for authentication operations
 * Handles user registration, login, and token validation
 */
@Service
@Transactional
public class AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Register a new user
     * @param registerRequest registration details
     * @return success/error message
     */
    public MessageResponse registerUser(RegisterRequest registerRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return new MessageResponse("Error: Username is already taken!", false);
        }
        
        // Create new user
        User user = new User(registerRequest.getUsername(),
                           passwordEncoder.encode(registerRequest.getPassword()));
        
        Set<String> strRoles = registerRequest.getRoles();
        Set<Role> roles = new HashSet<>();
        
        if (strRoles == null || strRoles.isEmpty()) {
            // Default role is USER
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> {
                        Role newRole = new Role("ROLE_USER");
                        return roleRepository.save(newRole);
                    });
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                                .orElseGet(() -> {
                                    Role newRole = new Role("ROLE_ADMIN");
                                    return roleRepository.save(newRole);
                                });
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName("ROLE_USER")
                                .orElseGet(() -> {
                                    Role newRole = new Role("ROLE_USER");
                                    return roleRepository.save(newRole);
                                });
                        roles.add(userRole);
                }
            });
        }
        
        user.setRoles(roles);
        userRepository.save(user);
        
        return new MessageResponse("User registered successfully!");
    }
    
    /**
     * Authenticate user and generate JWT token
     * @param loginRequest login credentials
     * @return JWT response with token and user details
     */
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        UserDetailsServiceImpl.UserPrincipal userPrincipal = 
                (UserDetailsServiceImpl.UserPrincipal) authentication.getPrincipal();
        
        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        
        String jwt = jwtUtil.generateJwtToken(authentication, roles);
        
        return new JwtResponse(jwt, userPrincipal.getUsername(), roles);
    }
    
    /**
     * Validate JWT token and return user details
     * @param token JWT token
     * @return user validation response
     */
    public UserValidationResponse validateToken(String token) {
        try {
            if (jwtUtil.validateJwtToken(token)) {
                String username = jwtUtil.getUsernameFromJwtToken(token);
                List<String> roles = jwtUtil.getRolesFromJwtToken(token);
                
                return new UserValidationResponse(username, roles, true);
            }
        } catch (Exception e) {
            // Token is invalid
        }
        
        return new UserValidationResponse(null, null, false);
    }
    
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