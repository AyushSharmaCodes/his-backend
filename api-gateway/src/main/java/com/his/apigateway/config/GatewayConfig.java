package com.his.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway Configuration for HIS Backend
 * 
 * Defines routing rules for different HIS microservices:
 * - Patient Service: Manages patient information and records
 * - Doctor Service: Manages doctor profiles and schedules
 * - Appointment Service: Handles appointment booking and management
 * - Billing Service: Processes billing and payment information
 * - Inventory Service: Manages medical inventory and supplies
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Patient Service Routes
                .route("patient-service", r -> r.path("/api/patients/**")
                        .uri("lb://patient-service"))
                
                // Doctor Service Routes
                .route("doctor-service", r -> r.path("/api/doctors/**")
                        .uri("lb://doctor-service"))
                
                // Appointment Service Routes
                .route("appointment-service", r -> r.path("/api/appointments/**")
                        .uri("lb://appointment-service"))
                
                // Billing Service Routes
                .route("billing-service", r -> r.path("/api/billing/**")
                        .uri("lb://billing-service"))
                
                // Inventory Service Routes
                .route("inventory-service", r -> r.path("/api/inventory/**")
                        .uri("lb://inventory-service"))
                
                // Auth Service Routes
                .route("auth-service", r -> r.path("/api/auth/**")
                        .uri("lb://auth-service"))
                
                // Notification Service Routes
                .route("notification-service", r -> r.path("/api/notifications/**")
                        .uri("lb://notification-service"))
                
                .build();
    }
}