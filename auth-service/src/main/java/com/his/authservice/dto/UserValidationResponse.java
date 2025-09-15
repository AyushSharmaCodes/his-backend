package com.his.authservice.dto;

import java.util.List;

/**
 * DTO for user validation response.
 */
public class UserValidationResponse {
    
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private boolean valid;
    
    // Constructors
    public UserValidationResponse() {}
    
    public UserValidationResponse(Long id, String username, String email, List<String> roles, boolean valid) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.valid = valid;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
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
    
    @Override
    public String toString() {
        return "UserValidationResponse{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                ", valid=" + valid +
                '}';
    }
}