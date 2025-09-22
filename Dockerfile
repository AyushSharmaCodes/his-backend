# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy maven files for dependency resolution
COPY pom.xml .
COPY service-registry/pom.xml service-registry/
COPY api-gateway/pom.xml api-gateway/

# Copy maven wrapper
COPY .mvn .mvn
COPY mvnw .

# Copy source code
COPY service-registry/src service-registry/src
COPY api-gateway/src api-gateway/src

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose ports
EXPOSE 8761 8080

# Create entrypoint script
RUN echo '#!/bin/bash\n\
if [ "$SERVICE_NAME" = "service-registry" ]; then\n\
    java -jar service-registry/target/service-registry-1.0.0.jar\n\
elif [ "$SERVICE_NAME" = "api-gateway" ]; then\n\
    java -jar api-gateway/target/api-gateway-1.0.0.jar\n\
else\n\
    echo "Please set SERVICE_NAME environment variable to either service-registry or api-gateway"\n\
    exit 1\n\
fi' > /app/entrypoint.sh

RUN chmod +x /app/entrypoint.sh

ENTRYPOINT ["/app/entrypoint.sh"]