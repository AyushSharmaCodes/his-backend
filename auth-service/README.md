# Auth Service - Role-Based Authentication Microservice

A Spring Boot microservice that provides JWT-based authentication and role-based authorization for the Hospital Information System (HIS).

## Features

- **JWT Authentication**: Secure token-based authentication
- **Role-Based Authorization**: Support for multiple user roles (ADMIN, DOCTOR, NURSE, USER)
- **Spring Security Integration**: Complete security configuration
- **Oracle Database Support**: Configured for Oracle database connectivity
- **RESTful API**: Clean REST endpoints for authentication operations
- **User Management**: User registration and validation
- **CORS Support**: Configured for cross-origin requests

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Oracle Database 11g or higher
- Git

## Project Structure

```
auth-service/
├── src/
│   ├── main/
│   │   ├── java/com/his/authservice/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── entity/         # JPA entities
│   │   │   ├── repository/     # JPA repositories
│   │   │   ├── security/       # Security components
│   │   │   ├── service/        # Business logic services
│   │   │   ├── util/           # Utility classes
│   │   │   └── AuthServiceApplication.java
│   │   └── resources/
│   │       └── application.yml  # Application configuration
│   └── test/                   # Test classes
├── pom.xml                     # Maven dependencies
└── README.md                   # This file
```

## Configuration

### Database Setup

1. **Oracle Database Configuration**:
   Update the `application.yml` file with your Oracle database credentials:

   ```yaml
   spring:
     datasource:
       url: jdbc:oracle:thin:@your-host:1521:your-sid
       username: your_username
       password: your_password
   ```

2. **JWT Configuration**:
   Update the JWT secret and expiration time:

   ```yaml
   jwt:
     secret: your-super-secret-key-at-least-256-bits-long
     expiration: 86400000  # 24 hours in milliseconds
   ```

### Environment Variables (Recommended for Production)

Set these environment variables instead of hardcoding values:

```bash
export DB_URL=jdbc:oracle:thin:@your-host:1521:your-sid
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export JWT_SECRET=your-super-secret-key
export JWT_EXPIRATION=86400000
```

## Building and Running

### Build the Application

```bash
cd auth-service
mvn clean compile
```

### Run Tests

```bash
mvn test
```

### Run the Application

```bash
mvn spring-boot:run
```

The service will start on `http://localhost:8080/auth`

## API Endpoints

### Authentication Endpoints

#### 1. User Registration
- **POST** `/auth/register`
- **Description**: Register a new user
- **Request Body**:
  ```json
  {
    "username": "john_doe",
    "password": "password123",
    "email": "john@example.com",
    "roles": ["user"]
  }
  ```
- **Response**:
  ```json
  {
    "message": "User registered successfully!",
    "username": "john_doe"
  }
  ```

#### 2. User Login
- **POST** `/auth/login`
- **Description**: Authenticate user and receive JWT token
- **Request Body**:
  ```json
  {
    "username": "john_doe",
    "password": "password123"
  }
  ```
- **Response**:
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "roles": ["ROLE_USER"]
  }
  ```

#### 3. Token Validation
- **GET** `/auth/validate`
- **Description**: Validate JWT token and get user info
- **Headers**: `Authorization: Bearer <token>`
- **Response**:
  ```json
  {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "roles": ["ROLE_USER"],
    "valid": true
  }
  ```

#### 4. Health Check
- **GET** `/auth/health`
- **Description**: Service health check
- **Response**:
  ```json
  {
    "status": "UP",
    "service": "auth-service"
  }
  ```

### Available Roles

- `ROLE_ADMIN`: Full system access
- `ROLE_DOCTOR`: Doctor-specific permissions
- `ROLE_NURSE`: Nurse-specific permissions
- `ROLE_USER`: Basic user permissions

## React Integration Guide

### 1. API Gateway Integration

When integrating with React, your requests should go through an API Gateway that routes to this auth service:

```
React App → API Gateway → Auth Service
```

**API Gateway Routes**:
- `POST /api/auth/register` → `auth-service/register`
- `POST /api/auth/login` → `auth-service/login`
- `GET /api/auth/validate` → `auth-service/validate`

### 2. React Authentication Setup

#### Install Required Dependencies

```bash
npm install axios
```

#### Create Authentication Service

```javascript
// services/authService.js
import axios from 'axios';

const API_URL = 'http://your-api-gateway-url/api/auth';

class AuthService {
  async login(username, password) {
    const response = await axios.post(`${API_URL}/login`, {
      username,
      password
    });
    
    if (response.data.token) {
      localStorage.setItem('user', JSON.stringify(response.data));
    }
    
    return response.data;
  }

  async register(username, password, email, roles = ['user']) {
    return axios.post(`${API_URL}/register`, {
      username,
      password,
      email,
      roles
    });
  }

  logout() {
    localStorage.removeItem('user');
  }

  getCurrentUser() {
    return JSON.parse(localStorage.getItem('user'));
  }

  getAuthHeader() {
    const user = JSON.parse(localStorage.getItem('user'));
    
    if (user && user.token) {
      return { Authorization: 'Bearer ' + user.token };
    } else {
      return {};
    }
  }

  async validateToken() {
    try {
      const response = await axios.get(`${API_URL}/validate`, {
        headers: this.getAuthHeader()
      });
      return response.data;
    } catch (error) {
      this.logout();
      return { valid: false };
    }
  }
}

export default new AuthService();
```

#### Create Axios Interceptor

```javascript
// services/api.js
import axios from 'axios';
import authService from './authService';

const api = axios.create({
  baseURL: 'http://your-api-gateway-url/api'
});

// Request interceptor to add auth header
api.interceptors.request.use(
  (config) => {
    const authHeader = authService.getAuthHeader();
    if (authHeader.Authorization) {
      config.headers.Authorization = authHeader.Authorization;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle auth errors
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      authService.logout();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

### 3. Protected Route Component

```javascript
// components/ProtectedRoute.js
import React, { useState, useEffect } from 'react';
import { Navigate } from 'react-router-dom';
import authService from '../services/authService';

const ProtectedRoute = ({ children, requiredRoles = [] }) => {
  const [loading, setLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [userRoles, setUserRoles] = useState([]);

  useEffect(() => {
    const checkAuth = async () => {
      const user = authService.getCurrentUser();
      
      if (!user) {
        setLoading(false);
        return;
      }

      try {
        const validation = await authService.validateToken();
        if (validation.valid) {
          setIsAuthenticated(true);
          setUserRoles(validation.roles || []);
        }
      } catch (error) {
        console.error('Token validation failed:', error);
      }
      
      setLoading(false);
    };

    checkAuth();
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // Check role-based access
  if (requiredRoles.length > 0) {
    const hasRequiredRole = requiredRoles.some(role => 
      userRoles.includes(`ROLE_${role.toUpperCase()}`)
    );
    
    if (!hasRequiredRole) {
      return <Navigate to="/unauthorized" replace />;
    }
  }

  return children;
};

export default ProtectedRoute;
```

### 4. Role-Based UI Rendering

```javascript
// hooks/useAuth.js
import { useState, useEffect, createContext, useContext } from 'react';
import authService from '../services/authService';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initializeAuth = async () => {
      const currentUser = authService.getCurrentUser();
      if (currentUser) {
        try {
          const validation = await authService.validateToken();
          if (validation.valid) {
            setUser(validation);
          } else {
            authService.logout();
          }
        } catch (error) {
          authService.logout();
        }
      }
      setLoading(false);
    };

    initializeAuth();
  }, []);

  const login = async (username, password) => {
    const userData = await authService.login(username, password);
    setUser(userData);
    return userData;
  };

  const logout = () => {
    authService.logout();
    setUser(null);
  };

  const hasRole = (role) => {
    return user?.roles?.includes(`ROLE_${role.toUpperCase()}`) || false;
  };

  const hasAnyRole = (roles) => {
    return roles.some(role => hasRole(role));
  };

  return (
    <AuthContext.Provider value={{
      user,
      loading,
      login,
      logout,
      hasRole,
      hasAnyRole,
      isAuthenticated: !!user
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
```

### 5. Role-Based Component Rendering

```javascript
// components/Navigation.js
import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

const Navigation = () => {
  const { user, hasRole, logout } = useAuth();

  return (
    <nav>
      <Link to="/">Home</Link>
      
      {hasRole('admin') && (
        <Link to="/admin-dashboard">Admin Dashboard</Link>
      )}
      
      {hasRole('doctor') && (
        <Link to="/doctor-dashboard">Doctor Dashboard</Link>
      )}
      
      {hasRole('nurse') && (
        <Link to="/nurse-dashboard">Nurse Dashboard</Link>
      )}
      
      <button onClick={logout}>Logout</button>
    </nav>
  );
};

export default Navigation;
```

### 6. App Component with Route Protection

```javascript
// App.js
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './hooks/useAuth';
import ProtectedRoute from './components/ProtectedRoute';
import Navigation from './components/Navigation';
import Home from './pages/Home';
import Login from './pages/Login';
import AdminDashboard from './pages/AdminDashboard';
import DoctorDashboard from './pages/DoctorDashboard';
import NurseDashboard from './pages/NurseDashboard';

function App() {
  return (
    <AuthProvider>
      <Router>
        <Navigation />
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/" element={
            <ProtectedRoute>
              <Home />
            </ProtectedRoute>
          } />
          <Route path="/admin-dashboard" element={
            <ProtectedRoute requiredRoles={['admin']}>
              <AdminDashboard />
            </ProtectedRoute>
          } />
          <Route path="/doctor-dashboard" element={
            <ProtectedRoute requiredRoles={['doctor']}>
              <DoctorDashboard />
            </ProtectedRoute>
          } />
          <Route path="/nurse-dashboard" element={
            <ProtectedRoute requiredRoles={['nurse']}>
              <NurseDashboard />
            </ProtectedRoute>
          } />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
```

## Security Considerations

1. **JWT Secret**: Use a strong, unique secret key (minimum 256 bits)
2. **HTTPS**: Always use HTTPS in production
3. **Token Storage**: Consider using httpOnly cookies instead of localStorage for sensitive applications
4. **Token Expiration**: Set appropriate token expiration times
5. **Password Policies**: Implement strong password requirements
6. **Rate Limiting**: Add rate limiting to prevent brute force attacks

## Monitoring and Health Checks

The service provides health check endpoints for monitoring:
- `/auth/health` - Basic health check
- `/actuator/health` - Spring Actuator health check

## Troubleshooting

### Common Issues

1. **Database Connection**: Verify Oracle database URL, username, and password
2. **JWT Validation Errors**: Check JWT secret configuration
3. **CORS Issues**: Verify CORS configuration matches your React app's origin
4. **Role Assignment**: Ensure roles are properly assigned during registration

### Logs

Check application logs for detailed error information. Set logging levels in `application.yml`:

```yaml
logging:
  level:
    com.his.authservice: DEBUG
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.