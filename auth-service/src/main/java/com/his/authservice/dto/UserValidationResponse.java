package com.his.authservice.dto;

import java.util.List;

/**
 * Data Transfer Object for user validation response
 */
public class UserValidationResponse {
    
    private String username;
    private List<String> roles;
    private boolean valid;
    
    // Default constructor
    public UserValidationResponse() {}
    
    // Constructor with parameters
    public UserValidationResponse(String username, List<String> roles, boolean valid) {
        this.username = username;
        this.roles = roles;
        this.valid = valid;
    }
    
    // Getters and Setters
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
    
    public boolean isValid() {
        return valid;
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }
}