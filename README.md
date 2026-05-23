# Outline OIDC Provider

🔐 An OIDC (OpenID Connect) authentication provider service as a lightweight alternative to Keycloak. Built with Spring Boot and Vue 3.

## Overview

This project implements a complete OIDC authentication service that can be deployed as a standalone service or integrated with existing applications. It provides user authentication, token management, and seamless integration with OpenID Connect clients.

### Key Features

- 🔑 **Full OIDC Support** - Authorization Code Flow, JWT tokens, and refresh token rotation
- 🌐 **JWT Authentication** - HS256 token signing with configurable TTL
- 💾 **PostgreSQL Backed** - Persistent user and session storage
- 🎨 **Customizable UI** - Login page with theme customization (logo, colors, background)
- 🐳 **Docker Ready** - Multi-stage Dockerfile for optimized container deployment
- ⚡ **Lightweight** - Alpine-based images, minimal dependencies
- 🔄 **Token Management** - Automatic token refresh and expiration handling

## Technology Stack

### Backend
- **Framework**: Spring Boot 2.7.18 (Spring Web, Spring Security, Spring Data JPA)
- **Language**: Java 8+
- **Build Tool**: Maven 3.9
- **Database**: PostgreSQL
- **Authentication**: JWT (HS256) with Nimbus JOSE

### Frontend
- **Framework**: Vue 3 (Composition API)
- **Build Tool**: Vite 6
- **HTTP Client**: Axios
- **State Management**: Pinia
- **Language**: TypeScript

### Infrastructure
- **Container**: Docker (Multi-stage build)
- **Orchestration**: Docker Compose
- **Reverse Proxy**: Traefik

## Quick Start

### Prerequisites

- Java 8+ (for local development)
- Node.js 18+ (for frontend development)
- PostgreSQL 12+ (for local development)
- Docker & Docker Compose (for containerized deployment)

### Local Development

#### 1. Backend Setup

```bash
cd backend

# Install dependencies and build
mvn clean install

# Run the application
mvn spring-boot:run
```

The backend will start on `http://localhost:8800`

#### 2. Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Start development server with proxy to backend
npm run dev
```

The frontend will start on `http://localhost:3000`

### Environment Variables

Create a `.env` file in the project root:

```env
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=keycloak
DB_USER=keycloak
DB_PASSWORD=your_secure_password

# OIDC Configuration
OIDC_ISSUER=http://oidc.outline.xyz:10087
OIDC_JWT_SECRET=your_256_bit_hex_secret_key

# Token TTLs
OIDC_CODE_TTL=300
OIDC_ACCESS_TOKEN_TTL=300
OIDC_REFRESH_TOKEN_TTL=86400

# Login Theme (Optional)
OIDC_LOGIN_BACKGROUND_URL=https://your-domain.com/background.jpg
OIDC_LOGIN_LOGO_URL=https://your-domain.com/logo.png
OIDC_LOGIN_THEME_COLOR=#007bff
```

### Docker Deployment

Build and run with Docker:

```bash
# Build the image
docker build -t outline-oidc-provider:latest .

# Run the container
docker run -d \
  -p 8800:8800 \
  -e OIDC_ISSUER=http://oidc.example.com \
  -e OIDC_JWT_SECRET=your_secret_key \
  -e DB_HOST=postgres \
  -e DB_USER=keycloak \
  -e DB_PASSWORD=password \
  outline-oidc-provider:latest
```

Or use Docker Compose (as a snippet in your compose configuration):

```yaml
services:
  oidc-service:
    build: ./oidc-service
    ports:
      - "8800:8800"
    environment:
      OIDC_ISSUER: http://oidc.example.com
      OIDC_JWT_SECRET: your_secret_key
      DB_HOST: postgres
      DB_USER: keycloak
      DB_PASSWORD: password
    depends_on:
      postgres:
        condition: service_healthy
    networks:
      - outline-network
```

## Project Structure

```
oidc-service/
├── backend/                    # Spring Boot OIDC server
│   ├── src/
│   │   ├── main/java/com/oidc/
│   │   │   ├── controller/    # REST endpoints
│   │   │   ├── service/       # Business logic
│   │   │   ├── model/         # JPA entities
│   │   │   ├── repository/    # Data access layer
│   │   │   └── config/        # Spring configuration
│   │   └── resources/
│   │       └── application.yml # Configuration
│   └── pom.xml
├── frontend/                   # Vue 3 login interface
│   ├── src/
│   │   ├── views/             # Page components
│   │   ├── api/               # API client
│   │   ├── router/            # Route configuration
│   │   └── App.vue            # Root component
│   ├── package.json
│   ├── vite.config.ts
│   └── tsconfig.json
├── Dockerfile                 # Multi-stage Docker build
├── compose-snippet.yml        # Docker Compose configuration
└── README.md
```

## API Endpoints

### Authentication
- `POST /oauth/authorize` - Authorization endpoint
- `POST /oauth/token` - Token endpoint
- `POST /oauth/revoke` - Token revocation

### OpenID Connect
- `GET /.well-known/openid-configuration` - OIDC Discovery endpoint
- `GET /jwks.json` - JSON Web Key Set

### User
- `GET /user` - Get current user info (requires valid token)

See [API Documentation](./docs/API.md) for detailed endpoint specifications.

## Configuration

### OIDC Issuer

The OIDC issuer URL must be set via environment variable:

```env
OIDC_ISSUER=http://oidc.example.com
```

### JWT Secret

A secure 256-bit hex secret must be provided:

```bash
# Generate a secure secret
openssl rand -hex 32
```

Then set it:
```env
OIDC_JWT_SECRET=your_generated_hex_string
```

### Token Lifetimes

Customize token expiration times (in seconds):

```env
OIDC_CODE_TTL=300           # Authorization code validity
OIDC_ACCESS_TOKEN_TTL=300   # Access token validity
OIDC_REFRESH_TOKEN_TTL=86400 # Refresh token validity (24 hours)
```

### Login Page Theme

Optional custom branding:

```env
OIDC_LOGIN_BACKGROUND_URL=https://example.com/bg.jpg
OIDC_LOGIN_LOGO_URL=https://example.com/logo.png
OIDC_LOGIN_THEME_COLOR=#007bff
OIDC_LOGIN_TITLE=My Organization Login
```

## Development

### Running Tests

**Backend:**
```bash
cd backend
mvn test
```

**Frontend:**
```bash
cd frontend
npm run test
```

### Building for Production

**Backend:**
```bash
cd backend
mvn clean package
```

**Frontend:**
```bash
cd frontend
npm run build
```

The frontend build output is embedded into the backend JAR file during Maven build.

## Deployment

### Kubernetes

Example Kubernetes deployment manifest:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: oidc-provider
spec:
  replicas: 1
  selector:
    matchLabels:
      app: oidc-provider
  template:
    metadata:
      labels:
        app: oidc-provider
    spec:
      containers:
      - name: oidc-provider
        image: outline-oidc-provider:latest
        ports:
        - containerPort: 8800
        env:
        - name: OIDC_ISSUER
          value: https://oidc.example.com
        - name: DB_HOST
          value: postgres-service
```

### Environment-Specific Configuration

Refer to [CONTRIBUTING.md](./CONTRIBUTING.md) for development environment setup.

## Database Schema

The application automatically initializes the PostgreSQL database schema on first run. Supported tables include:

- `users` - User accounts
- `clients` - OAuth2 clients
- `tokens` - Token history and blacklist
- `sessions` - User sessions

## Troubleshooting

### Connection Issues
- Verify PostgreSQL is running and accessible
- Check database credentials in environment variables
- Ensure network connectivity between services

### Token Issues
- Validate JWT secret format (must be hex string)
- Check token TTL settings
- Verify OIDC issuer URL is correctly configured

### Frontend Issues
- Clear browser cache and session storage
- Verify Vite dev server is running on port 3000
- Check browser console for CORS errors

## Contributing

We welcome contributions! Please see [CONTRIBUTING.md](./CONTRIBUTING.md) for guidelines on how to:

- Set up the development environment
- Follow code standards
- Submit pull requests
- Report issues

## License

This project is licensed under the MIT License - see [LICENSE](./LICENSE) file for details.

## Support

For issues, questions, or suggestions:

1. Check existing [Issues](https://github.com/ho1yman/outline-oidc-provider/issues)
2. Create a new issue with detailed description
3. Include environment details and steps to reproduce

## Acknowledgments

This OIDC provider was designed as a lightweight alternative to Keycloak, built specifically for the Outline project but suitable for independent use.

---

**Last Updated**: May 2026  
**Current Version**: 1.0.0
