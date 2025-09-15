# Authentication Service (auth-service)

A role-based authentication microservice built with Spring Boot, Spring Security, and JWT tokens for the HIS (Hospital Information System) backend.

## Table of Contents
1. [Overview](#overview)
2. [Technologies Used](#technologies-used)
3. [Project Structure](#project-structure)
4. [Setup and Configuration](#setup-and-configuration)
5. [API Endpoints](#api-endpoints)
6. [React Integration Guide](#react-integration-guide)
7. [Testing](#testing)
8. [Production Deployment](#production-deployment)

## Overview

This microservice provides:
- User registration and authentication
- JWT-based stateless authentication
- Role-based authorization (USER, ADMIN)
- Token validation for other microservices
- Oracle database integration

## Technologies Used

- **Spring Boot 3.2.0** - Application framework
- **Spring Security 6.x** - Authentication and authorization
- **Spring Data JPA** - Data persistence layer
- **Hibernate** - ORM framework
- **Oracle Database** - Primary database
- **JWT (JSON Web Tokens)** - Stateless authentication
- **Maven** - Dependency management
- **BCrypt** - Password encryption

## Project Structure

```
auth-service/
├── src/
│   ├── main/
│   │   ├── java/com/his/authservice/
│   │   │   ├── config/           # Security and application configuration
│   │   │   ├── controller/       # REST controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── repository/      # Data access layer
│   │   │   ├── service/         # Business logic
│   │   │   ├── util/            # Utility classes (JWT)
│   │   │   └── AuthServiceApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/                    # Test classes
├── pom.xml
└── README.md
```

## Setup and Configuration

### 1. Database Configuration

Edit `src/main/resources/application.properties`:

```properties
# Oracle Database Configuration
spring.datasource.url=jdbc:oracle:thin:@your-oracle-host:1521:your-sid
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

# JWT Configuration
app.jwt.secret=your-256-bit-secret-key-here-make-it-strong-and-unique
app.jwt.expiration=86400000  # 24 hours in milliseconds
```

### 2. Build and Run

```bash
# Navigate to auth-service directory
cd auth-service

# Build the application
mvn clean install

# Run the application
mvn spring-boot:run
```

The service will start on `http://localhost:8081/auth-service`

### 3. Database Schema

The application will automatically create the following tables:
- `users` - User information
- `roles` - Available roles
- `user_roles` - Many-to-many relationship between users and roles

## API Endpoints

### Base URL: `http://localhost:8081/auth-service/auth`

### 1. User Registration
```http
POST /auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "password": "securePassword123",
  "roles": ["user"]  // Optional: ["user", "admin"]
}
```

**Response:**
```json
{
  "message": "User registered successfully!",
  "success": true
}
```

### 2. User Login
```http
POST /auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "username": "john_doe",
  "roles": ["ROLE_USER"]
}
```

### 3. Token Validation
```http
GET /auth/validate
Authorization: Bearer <your-jwt-token>
```

**Response:**
```json
{
  "username": "john_doe",
  "roles": ["ROLE_USER"],
  "valid": true
}
```

### 4. Test Endpoints
```http
# User access (requires USER or ADMIN role)
GET /auth/user
Authorization: Bearer <your-jwt-token>

# Admin access (requires ADMIN role only)
GET /auth/admin
Authorization: Bearer <your-jwt-token>
```

## React Integration Guide

### Architecture Overview

```
React App → API Gateway → auth-service
                      ↓
                  Other Microservices
```

### 1. Setting Up API Client

Create an API client utility:

```javascript
// src/utils/apiClient.js
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080'; // API Gateway URL
const AUTH_SERVICE_PATH = '/auth-service/auth';

// Create axios instance
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
});

// Add request interceptor to include auth token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add response interceptor to handle auth errors
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid
      localStorage.removeItem('authToken');
      localStorage.removeItem('userRoles');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;
```

### 2. Authentication Service

```javascript
// src/services/authService.js
import apiClient from '../utils/apiClient';

const AUTH_SERVICE_PATH = '/auth-service/auth';

export const authService = {
  // Register new user
  register: async (userData) => {
    const response = await apiClient.post(`${AUTH_SERVICE_PATH}/register`, userData);
    return response.data;
  },

  // Login user
  login: async (credentials) => {
    const response = await apiClient.post(`${AUTH_SERVICE_PATH}/login`, credentials);
    const { token, username, roles } = response.data;
    
    // Store token and user info
    localStorage.setItem('authToken', token);
    localStorage.setItem('username', username);
    localStorage.setItem('userRoles', JSON.stringify(roles));
    
    return response.data;
  },

  // Logout user
  logout: () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('username');
    localStorage.removeItem('userRoles');
  },

  // Validate token
  validateToken: async () => {
    try {
      const response = await apiClient.get(`${AUTH_SERVICE_PATH}/validate`);
      return response.data;
    } catch (error) {
      return { valid: false };
    }
  },

  // Get current user info
  getCurrentUser: () => {
    return {
      username: localStorage.getItem('username'),
      roles: JSON.parse(localStorage.getItem('userRoles') || '[]'),
      token: localStorage.getItem('authToken')
    };
  },

  // Check if user is authenticated
  isAuthenticated: () => {
    return !!localStorage.getItem('authToken');
  },

  // Check if user has specific role
  hasRole: (role) => {
    const roles = JSON.parse(localStorage.getItem('userRoles') || '[]');
    return roles.includes(role);
  }
};
```

### 3. Authentication Context

```javascript
// src/contexts/AuthContext.js
import React, { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../services/authService';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check if user is logged in on app start
    const initAuth = async () => {
      if (authService.isAuthenticated()) {
        const validation = await authService.validateToken();
        if (validation.valid) {
          setUser(authService.getCurrentUser());
        } else {
          authService.logout();
        }
      }
      setLoading(false);
    };

    initAuth();
  }, []);

  const login = async (credentials) => {
    const userData = await authService.login(credentials);
    setUser(authService.getCurrentUser());
    return userData;
  };

  const logout = () => {
    authService.logout();
    setUser(null);
  };

  const register = async (userData) => {
    return await authService.register(userData);
  };

  const hasRole = (role) => {
    return user?.roles.includes(role) || false;
  };

  const value = {
    user,
    login,
    logout,
    register,
    hasRole,
    isAuthenticated: !!user,
    loading
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};
```

### 4. Protected Route Component

```javascript
// src/components/ProtectedRoute.js
import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const ProtectedRoute = ({ children, requiredRole = null }) => {
  const { isAuthenticated, hasRole, loading } = useAuth();
  const location = useLocation();

  if (loading) {
    return <div>Loading...</div>; // Or a loading spinner component
  }

  if (!isAuthenticated) {
    // Redirect to login page with return url
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (requiredRole && !hasRole(requiredRole)) {
    // User doesn't have required role
    return <Navigate to="/unauthorized" replace />;
  }

  return children;
};

export default ProtectedRoute;
```

### 5. Role-Based UI Components

```javascript
// src/components/RoleBasedComponent.js
import React from 'react';
import { useAuth } from '../contexts/AuthContext';

const RoleBasedComponent = ({ children, roles = [], fallback = null }) => {
  const { hasRole } = useAuth();

  const hasRequiredRole = roles.some(role => hasRole(role));

  if (!hasRequiredRole) {
    return fallback;
  }

  return children;
};

export default RoleBasedComponent;
```

### 6. Usage Examples

#### App Component with Routes
```javascript
// src/App.js
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import RoleBasedComponent from './components/RoleBasedComponent';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import AdminPanel from './pages/AdminPanel';
import Navigation from './components/Navigation';

function App() {
  return (
    <AuthProvider>
      <Router>
        <Navigation />
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route 
            path="/dashboard" 
            element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/admin" 
            element={
              <ProtectedRoute requiredRole="ROLE_ADMIN">
                <AdminPanel />
              </ProtectedRoute>
            } 
          />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
```

#### Navigation with Role-Based Links
```javascript
// src/components/Navigation.js
import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import RoleBasedComponent from './RoleBasedComponent';

const Navigation = () => {
  const { isAuthenticated, user, logout } = useAuth();

  return (
    <nav className="navbar">
      <div className="nav-brand">
        <Link to="/">HIS System</Link>
      </div>
      
      <div className="nav-links">
        {isAuthenticated ? (
          <>
            <Link to="/dashboard">Dashboard</Link>
            
            {/* Admin-only link */}
            <RoleBasedComponent roles={['ROLE_ADMIN']}>
              <Link to="/admin">Admin Panel</Link>
            </RoleBasedComponent>
            
            <span>Welcome, {user.username}</span>
            <button onClick={logout}>Logout</button>
          </>
        ) : (
          <Link to="/login">Login</Link>
        )}
      </div>
    </nav>
  );
};

export default Navigation;
```

#### Login Component
```javascript
// src/pages/Login.js
import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const Login = () => {
  const [credentials, setCredentials] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const from = location.state?.from?.pathname || '/dashboard';

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await login(credentials);
      navigate(from, { replace: true });
    } catch (error) {
      setError(error.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-form">
      <h2>Login</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <input
            type="text"
            placeholder="Username"
            value={credentials.username}
            onChange={(e) => setCredentials({...credentials, username: e.target.value})}
            required
          />
        </div>
        <div>
          <input
            type="password"
            placeholder="Password"
            value={credentials.password}
            onChange={(e) => setCredentials({...credentials, password: e.target.value})}
            required
          />
        </div>
        {error && <div className="error">{error}</div>}
        <button type="submit" disabled={loading}>
          {loading ? 'Logging in...' : 'Login'}
        </button>
      </form>
    </div>
  );
};

export default Login;
```

### 7. Token Storage Strategy

#### Option 1: localStorage (Simple, persistent)
```javascript
// Good for: Single-tab applications, longer sessions
// Pros: Survives browser restart, simple to implement
// Cons: Vulnerable to XSS attacks, shared across tabs

localStorage.setItem('authToken', token);
```

#### Option 2: sessionStorage (Session-only)
```javascript
// Good for: More secure applications, session-based auth
// Pros: Cleared when tab closes, more secure than localStorage
// Cons: Lost on tab close, not shared across tabs

sessionStorage.setItem('authToken', token);
```

#### Option 3: HTTP-only Cookies (Most secure)
```javascript
// Good for: High-security applications
// Pros: Not accessible via JavaScript, immune to XSS
// Cons: Requires backend cookie handling, CSRF protection needed
// Note: Requires changes to both React and Spring Boot
```

### 8. Security Best Practices

1. **Always use HTTPS in production**
2. **Implement proper CORS configuration**
3. **Set appropriate token expiration times**
4. **Validate tokens on critical operations**
5. **Implement refresh token mechanism for long sessions**
6. **Clear tokens on logout**
7. **Handle token expiration gracefully**

### 9. Error Handling

```javascript
// src/utils/errorHandler.js
export const handleAuthError = (error) => {
  if (error.response?.status === 401) {
    // Unauthorized - redirect to login
    authService.logout();
    window.location.href = '/login';
  } else if (error.response?.status === 403) {
    // Forbidden - show access denied message
    return 'Access denied. You do not have permission to perform this action.';
  } else if (error.response?.status >= 500) {
    // Server error
    return 'Server error. Please try again later.';
  } else {
    // Other errors
    return error.response?.data?.message || 'An error occurred';
  }
};
```

## Testing

### Running Tests
```bash
mvn test
```

### Manual Testing with cURL

```bash
# Register a user
curl -X POST http://localhost:8081/auth-service/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123","roles":["user"]}'

# Login
curl -X POST http://localhost:8081/auth-service/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'

# Validate token (replace TOKEN with actual token)
curl -X GET http://localhost:8081/auth-service/auth/validate \
  -H "Authorization: Bearer TOKEN"
```

## Production Deployment

### Environment Variables
Set these environment variables in production:

```bash
SPRING_DATASOURCE_URL=jdbc:oracle:thin:@prod-oracle-host:1521:PROD
SPRING_DATASOURCE_USERNAME=prod_user
SPRING_DATASOURCE_PASSWORD=secure_prod_password
APP_JWT_SECRET=very-long-and-secure-production-secret-key-256-bits
APP_JWT_EXPIRATION=3600000  # 1 hour for production
```

### Security Considerations
1. Use a strong JWT secret (256+ bits)
2. Set shorter token expiration in production
3. Implement HTTPS/TLS
4. Configure proper CORS policies
5. Enable request rate limiting
6. Monitor for suspicious authentication attempts
7. Implement proper logging and auditing

---

## Support

For issues and questions:
1. Check the application logs
2. Verify database connectivity
3. Ensure JWT secret is properly configured
4. Check CORS settings for React integration

## License

This project is part of the HIS (Hospital Information System) backend.