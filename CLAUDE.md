# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

拳馆管理系统 (Boxing Gym Management System) — A monorepo with Spring Boot 3 backend and Vue 3 frontend.

## Common Commands

### Backend (boxing-gym-backend/)
```bash
mvn spring-boot:run           # Start server on port 8080
mvn -DskipTests compile       # Compile without tests
mvn -DskipTests package       # Build executable JAR
```

### Frontend (boxing-gym-frontend/)
```bash
npm install                   # Install dependencies (requires Node.js 20 LTS, not 24)
npm run dev                   # Start dev server on port 3000
npm run build                 # Production build
npm run lint                  # Run ESLint with auto-fix
```

### Database
```bash
mysql -u root -p < boxing-gym-backend/src/main/resources/sql/init.sql
```

## Architecture

### Backend Layer Structure
```
src/main/java/com/boxinggym/
├── controller/    # REST endpoints (minimal, only annotations)
├── service/       # Interface definitions
├── service/impl/  # Business logic implementations
├── mapper/        # MyBatis-Plus interfaces for DB operations
├── entity/        # Database entities
├── dto/           # Request/response DTOs
├── vo/            # Value Objects for responses
├── config/        # Spring configurations (MyBatis, Redis, Security, etc.)
├── security/      # JWT authentication, Spring Security setup
└── common/        # Result<T> unified response wrapper
```

### Key Patterns
- **Clean Architecture**: Strict layer separation
- **DTO-Entity Separation**: Never expose entities directly in APIs
- **Service Layer**: All business logic in `service/impl/`
- **MyBatis-Plus**: Auto CRUD with mapper interfaces, XML for complex queries in `resources/mapper/`
- **Unified Response**: All APIs return `Result<T>` wrapper
- **JWT Auth**: Spring Security with JWT tokens

### Frontend Structure
```
src/
├── api/          # Axios request wrappers
├── store/        # Pinia state management
├── router/       # Vue Router configuration
├── views/        # Page components
├── components/   # Reusable components
├── types/        # TypeScript definitions
└── utils/        # Helper functions
```

## Coding Conventions

### Backend
- Java 17, Spring Boot 3.2, Lombok
- Methods must not exceed 50 lines
- JavaDoc required for all public methods
- Controllers contain only annotations, no business logic
- Use design patterns where appropriate

### Frontend
- Vue 3 Composition API with `<script setup>`
- TypeScript strict mode
- Element Plus for UI components
- API proxy: `/api` → `http://localhost:8080` (dev)

## Environment Requirements

- JDK 17+
- MySQL 8+
- Redis
- Maven 3.9+
- Node.js 20 LTS (Node 24 has vue-tsc compatibility issues)

## Default Credentials

- Admin login: `admin` / `admin123`
- Druid monitoring: `admin` / `admin123` at `/druid/`

## Useful Endpoints

- API: `http://localhost:8080`
- API Docs (Knife4j): `http://localhost:8080/doc.html`
- Druid Monitor: `http://localhost:8080/druid/`
- Frontend: `http://localhost:3000`
