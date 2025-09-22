package com.his.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Fallback Controller for API Gateway
 * 
 * Provides fallback responses when downstream services are unavailable
 */
@RestController
public class FallbackController {

    @GetMapping("/fallback")
    public ResponseEntity<Map<String, Object>> fallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Service is temporarily unavailable. Please try again later.");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("timestamp", LocalDateTime.now());
        response.put("errorCode", "HIS_SERVICE_DOWN");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "api-gateway");
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
}