#!/bin/bash

# HIS Backend Startup Script

echo "Starting HIS Backend Infrastructure..."

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Java is not installed. Please install Java 17 or higher."
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed. Please install Maven."
    exit 1
fi

# Build the projects
echo "Building projects..."
mvn clean package -DskipTests

# Start Service Registry
echo "Starting Service Registry on port 8761..."
cd service-registry
java -jar target/service-registry-1.0.0.jar &
SERVICE_REGISTRY_PID=$!
cd ..

# Wait for Service Registry to start
echo "Waiting for Service Registry to start..."
sleep 30

# Start API Gateway
echo "Starting API Gateway on port 8080..."
cd api-gateway
java -jar target/api-gateway-1.0.0.jar &
API_GATEWAY_PID=$!
cd ..

echo "HIS Backend Infrastructure started successfully!"
echo "Service Registry: http://localhost:8761"
echo "API Gateway: http://localhost:8080"
echo ""
echo "To stop the services, run: kill $SERVICE_REGISTRY_PID $API_GATEWAY_PID"

# Create a simple stop script
cat > stop.sh << EOF
#!/bin/bash
echo "Stopping HIS Backend Infrastructure..."
kill $SERVICE_REGISTRY_PID $API_GATEWAY_PID
echo "Services stopped."
EOF

chmod +x stop.sh

# Wait for services to run
wait