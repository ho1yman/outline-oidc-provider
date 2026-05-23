# Contributing to Outline OIDC Provider

Thank you for your interest in contributing to the Outline OIDC Provider! This document provides guidelines and instructions for contributing to the project.

## Code of Conduct

We are committed to providing a welcoming and inspiring community for all. Please read and adhere to our values of respect, inclusivity, and professionalism in all interactions.

## How to Contribute

### Reporting Bugs

Before creating a bug report, please check the issue list to avoid duplicates. When creating a bug report, include:

- **Title**: Clear, descriptive summary
- **Environment**: OS, Java version, Node.js version, Docker version
- **Steps to reproduce**: Exact steps to reproduce the issue
- **Expected behavior**: What should happen
- **Actual behavior**: What actually happens
- **Screenshots/logs**: If applicable, include error messages

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When suggesting an enhancement:

- **Title**: Clear, descriptive summary
- **Description**: Detailed explanation of the enhancement
- **Use case**: Why this enhancement would be useful
- **Examples**: Any examples or mockups

### Pull Requests

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Make your changes
4. Write or update tests as needed
5. Ensure code follows project style guidelines
6. Commit with clear messages (`git commit -m 'Add some AmazingFeature'`)
7. Push to the branch (`git push origin feature/AmazingFeature`)
8. Open a Pull Request with a detailed description

## Development Setup

### Prerequisites

- Java 8 or higher
- Maven 3.9+
- Node.js 18+
- PostgreSQL 12+ (for local development)
- Git

### Backend Development

1. **Clone and navigate to backend:**
   ```bash
   git clone https://github.com/ho1yman/outline-oidc-provider.git
   cd outline-oidc-provider/backend
   ```

2. **Install dependencies:**
   ```bash
   mvn clean install
   ```

3. **Configure local database:**
   ```bash
   # Create PostgreSQL database
   createdb -U postgres keycloak
   
   # Or use Docker:
   docker run --name postgres-oidc -e POSTGRES_PASSWORD=password \
     -e POSTGRES_DB=keycloak -p 5432:5432 -d postgres:15
   ```

4. **Set environment variables:**
   ```bash
   export DB_HOST=localhost
   export DB_USER=postgres
   export DB_PASSWORD=password
   export OIDC_JWT_SECRET=your_256_bit_hex_secret
   export OIDC_ISSUER=http://localhost:8800
   ```

5. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

   Backend will be available at `http://localhost:8800`

6. **Run tests:**
   ```bash
   mvn test
   ```

### Frontend Development

1. **Navigate to frontend:**
   ```bash
   cd outline-oidc-provider/frontend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Start development server:**
   ```bash
   npm run dev
   ```

   Frontend will be available at `http://localhost:3000`

4. **Build for production:**
   ```bash
   npm run build
   ```

5. **Run tests (if configured):**
   ```bash
   npm run test
   ```

### Testing Backend Changes with Frontend

The `vite.config.ts` includes a proxy configuration that forwards API requests to the backend:

```typescript
server: {
  proxy: {
    '/api': 'http://localhost:8800',
    '/realms': 'http://localhost:8800'
  }
}
```

This allows you to:
1. Run backend on `localhost:8800`
2. Run frontend on `localhost:3000`
3. Frontend will proxy API calls to backend

## Code Standards

### Backend (Java)

- **Style**: Follow Google Java Style Guide
- **Naming**: Use clear, descriptive names for classes and methods
- **Comments**: Add comments for complex logic
- **Formatting**: Use `mvn spotless:apply` to auto-format code
- **Tests**: Aim for >80% code coverage

Example code style:
```java
// Good: descriptive, clear
public ResponseEntity<UserDTO> getUserByEmail(String email) {
    User user = userRepository.findByEmail(email);
    return ResponseEntity.ok(convertToDTO(user));
}

// Avoid: unclear naming
public ResponseEntity<?> getUser(String s) {
    // ...
}
```

### Frontend (Vue/TypeScript)

- **Style**: Follow Vue 3 and TypeScript best practices
- **Naming**: Use camelCase for variables, PascalCase for components
- **Composition API**: Use `<script setup>` syntax when possible
- **Typing**: Provide proper TypeScript types
- **Linting**: Run `npm run lint` before committing

Example Vue 3 style:
```typescript
// Good: proper typing, clear naming
<script setup lang="ts">
import { ref, computed } from 'vue'

interface User {
  id: string
  email: string
}

const user = ref<User | null>(null)
const isAdmin = computed(() => user.value?.role === 'admin')
</script>

// Avoid: implicit any, poor naming
<script setup>
const u = ref()
const isA = computed(() => u.value?.role === 'admin')
</script>
```

### Git Commit Messages

Follow conventional commit format:

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types**: feat, fix, docs, style, refactor, test, chore, ci  
**Scope**: Optional, e.g., backend, frontend, docker  
**Subject**: Imperative, present tense, no period

Examples:
```
feat(backend): add user logout endpoint
fix(frontend): correct JWT token refresh timing
docs: update installation instructions
test(backend): add user service tests
```

## Branching Strategy

- **main**: Production-ready code, must be stable
- **develop**: Integration branch for features (optional, use main if preferred)
- **feature/\***: New features (`feature/user-management`)
- **bugfix/\***: Bug fixes (`bugfix/login-issue`)
- **docs/\***: Documentation updates (`docs/api-guide`)

## Review Process

1. **Code Review**: At least one maintainer review is required
2. **Tests**: All tests must pass (CI/CD pipeline)
3. **Documentation**: Update docs if behavior changes
4. **Merge**: Squash and merge for cleaner history

## Development Workflow Example

```bash
# 1. Create feature branch
git checkout -b feature/user-registration

# 2. Make changes
# ... edit files ...

# 3. Test locally
mvn test           # backend
npm run test       # frontend

# 4. Commit changes
git add .
git commit -m "feat(backend): add user registration endpoint"

# 5. Push and create PR
git push origin feature/user-registration
# Create pull request on GitHub

# 6. Address feedback
# ... make requested changes ...

# 7. Merge after approval
```

## Building and Releasing

### Local Build

```bash
# Build the complete project (includes frontend in backend JAR)
mvn clean package -DskipTests

# Build Docker image
docker build -t outline-oidc-provider:dev .
```

### Release Process

- Maintainers handle version updates and releases
- Follow semantic versioning: MAJOR.MINOR.PATCH
- Create GitHub Release with changelog

## Getting Help

- **Documentation**: Check [README.md](./README.md)
- **Issues**: Search existing issues or create a new one
- **Discussions**: Use GitHub Discussions for questions

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

Thank you for contributing to making Outline OIDC Provider better! 🎉
