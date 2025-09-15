package com.his.authservice.dto;

import java.util.List;

/**
 * Data Transfer Object for JWT authentication response
 */
public class JwtResponse {
    
    private String token;
    private String type = "Bearer";
    private String username;
    private List<String> roles;
    
    // Default constructor
    public JwtResponse() {}
    
    // Constructor with parameters
    public JwtResponse(String accessToken, String username, List<String> roles) {
        this.token = accessToken;
        this.username = username;
        this.roles = roles;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public List<String> getRoles() {
        return roles;
    }
    
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}