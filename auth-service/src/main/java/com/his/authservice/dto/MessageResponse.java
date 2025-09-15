package com.his.authservice.dto;

/**
 * Data Transfer Object for API response messages
 */
public class MessageResponse {
    
    private String message;
    private boolean success;
    
    // Default constructor
    public MessageResponse() {}
    
    // Constructor with message
    public MessageResponse(String message) {
        this.message = message;
        this.success = true;
    }
    
    // Constructor with message and success flag
    public MessageResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
    
    // Getters and Setters
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
}