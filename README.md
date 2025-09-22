# HIS Backend - Hospital Information System

A comprehensive Hospital Information System backend built with microservices architecture using Spring Boot and Spring Cloud.

## Architecture Overview

This project implements a microservices architecture with the following core components:

### Core Infrastructure Services

1. **Service Registry (Eureka Server)** - Port 8761
   - Central service discovery and registration
   - Manages all microservice instances
   - Provides service health monitoring

2. **API Gateway (Spring Cloud Gateway)** - Port 8080
   - Single entry point for all client requests
   - Load balancing and routing
   - Cross-cutting concerns (CORS, circuit breaker)
   - Fallback mechanisms for service failures

### Planned Microservices

The API Gateway is pre-configured to route to the following HIS microservices:

- **Patient Service** (`/api/patients/**`) - Patient information and medical records
- **Doctor Service** (`/api/doctors/**`) - Doctor profiles and schedules
- **Appointment Service** (`/api/appointments/**`) - Appointment booking and management
- **Billing Service** (`/api/billing/**`) - Billing and payment processing
- **Inventory Service** (`/api/inventory/**`) - Medical inventory and supplies
- **Auth Service** (`/api/auth/**`) - Authentication and authorization
- **Notification Service** (`/api/notifications/**`) - Email/SMS notifications

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker (optional)

### Running Locally

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd his-backend
   ```

2. **Build the projects**
   ```bash
   mvn clean package -DskipTests
   ```

3. **Start using the convenience script**
   ```bash
   ./start.sh
   ```

4. **Or start services manually**
   ```bash
   # Start Service Registry
   cd service-registry
   java -jar target/service-registry-1.0.0.jar &
   
   # Wait for service registry to start, then start API Gateway
   cd ../api-gateway
   java -jar target/api-gateway-1.0.0.jar &
   ```

### Running with Docker

```bash
# Build and start all services
docker-compose up --build

# Run in background
docker-compose up -d --build
```

## Service URLs

Once started, the following services will be available:

- **Service Registry Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **API Gateway Health**: http://localhost:8080/health
- **Service Registry Health**: http://localhost:8761/actuator/health

## Configuration

### Service Registry Configuration

The Eureka server is configured with:
- No self-registration (server mode)
- Self-preservation disabled for development
- Custom eviction interval for faster service deregistration

### API Gateway Configuration

The API Gateway includes:
- Service discovery integration
- Load balancing
- CORS support
- Circuit breaker patterns
- Request timeout configuration
- Fallback mechanisms

## Development

### Adding New Microservices

1. Create a new Spring Boot application
2. Add Eureka client dependency
3. Configure the service name in `application.properties`
4. Register with Eureka: `eureka.client.service-url.defaultZone=http://localhost:8761/eureka/`
5. Add routing rules in API Gateway's `GatewayConfig.java`

### Project Structure

```
his-backend/
├── service-registry/          # Eureka Service Registry
│   ├── src/main/java/
│   └── pom.xml
├── api-gateway/              # Spring Cloud Gateway
│   ├── src/main/java/
│   └── pom.xml
├── docker-compose.yml        # Docker orchestration
├── Dockerfile               # Multi-service Docker image
├── start.sh                # Local startup script
└── pom.xml                 # Parent POM
```

## Monitoring and Health Checks

Both services expose Spring Boot Actuator endpoints:

- **Service Registry**: http://localhost:8761/actuator/health
- **API Gateway**: http://localhost:8080/actuator/health

The API Gateway also exposes gateway-specific endpoints:
- **Gateway Routes**: http://localhost:8080/actuator/gateway/routes

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.