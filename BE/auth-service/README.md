# Auth Service

Microservice for user authentication, registration, and password recovery using Spring Boot and PostgreSQL.

## Features

- User Registration
- User Login with JWT
- Password Recovery via Email
- Password Reset

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose

## Setup

### 1. Start PostgreSQL Database

```bash
cd BE
docker-compose up -d
```

This will start a PostgreSQL container on port 5432.

### 2. Configure Email (Optional)

For password recovery to work, configure email settings in `application.properties`:

```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

Or set environment variables:
```bash
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-app-password
```

### 3. Build and Run

```bash
cd auth-service
mvn clean install
mvn spring-boot:run
```

The service will start on `http://localhost:8080`

## API Endpoints

### Register
```
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

### Login
```
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

### Request Password Recovery
```
POST /api/auth/password-recovery
Content-Type: application/json

{
  "email": "user@example.com"
}
```

### Reset Password
```
POST /api/auth/reset-password
Content-Type: application/json

{
  "token": "recovery-token-from-email",
  "newPassword": "newpassword123"
}
```

## Database

The service uses PostgreSQL. The database schema is automatically created on startup using JPA.

## Security

- Passwords are hashed using BCrypt
- JWT tokens are used for authentication
- Password recovery tokens expire after 24 hours
