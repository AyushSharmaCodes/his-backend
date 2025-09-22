package com.his.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway Application for HIS Backend
 * 
 * This service acts as the single entry point for all client requests.
 * It routes requests to appropriate microservices based on configuration
 * and provides cross-cutting concerns like authentication, logging, and rate limiting.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}