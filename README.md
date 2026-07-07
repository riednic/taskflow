# TaskFlow Workflow Engine

TaskFlow is a workflow management platform that allows users to create, manage, and process tasks through a predefined workflow state machine.

The application provides:

- Task lifecycle management
- Role-based access control
- JWT-based authentication
- Optimistic locking for concurrent updates
- Audit logging of workflow transitions
- Asynchronous notifications
- Statistics and reporting endpoints

The goal of this project is to demonstrate a production-oriented backend architecture using Kotlin and Spring Boot.

---

# Architecture Overview

The application follows a layered architecture with clear separation between API, business logic, and persistence concerns.

---

# Implementation Plan

The implementation is split into incremental feature-based steps.

1. Setup Spring Boot + PostgreSQL + Flyway
2. Implement User + Task Domain Model
3. Implement Task CRUD API
4. Implement Workflow State Machine
5. Add Optimistic Locking
6. Implement JWT Authentication
7. Implement RBAC Authorization
8. Add Transition Audit Logging
9. Add Async Notifications
10. Add Statistics API
11. Add Swagger + Docker + Documentation
12. Add Tests + Cleanup

---

# Technology Stack

## Backend

- Kotlin
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate ORM

## Database

- PostgreSQL
- Flyway Database Migrations

## Security

- Spring Security
- JWT Authentication

## Additional Components

- Redis (planned)
    - Token revocation
    - Caching

- Docker Compose
    - Local development environment

---

# Design Decisions

## Framework Choice

### Spring Boot

Spring Boot was chosen as the main backend framework.

Alternatives considered:

### Ktor

Ktor is a lightweight Kotlin backend framework.

Advantages:

- Kotlin-native
- Lightweight
- Flexible

Disadvantages:

- Requires more manual setup
- Less built-in enterprise functionality

For this project Spring Boot was preferred because it provides many production-ready features out of the box:

- Dependency injection
- Security integration
- Database integration
- Validation
- Testing support
- Mature ecosystem

### Quarkus

Quarkus would also be a valid alternative.

Advantages:

- Fast startup times
- Cloud-native focus
- Good container support

However, Spring Boot was chosen due to its maturity, ecosystem size, and widespread usage in enterprise environments.

---

## Database Choice

### PostgreSQL

PostgreSQL was chosen as the primary database.

Reasons:

- Mature relational database
- Strong consistency guarantees
- Excellent support with Hibernate
- Good performance for transactional workloads

Alternatives considered:

- MySQL
- MariaDB
- H2 (mainly for testing)

H2 could be used for lightweight integration tests, while PostgreSQL represents a more realistic production environment.

---

## ORM Choice

### JPA / Hibernate

Hibernate was chosen as ORM because:

- Reduces boilerplate SQL
- Integrates well with Spring Data
- Provides transaction management
- Supports optimistic locking via `@Version`

The application still uses explicit queries where required, especially for performance-critical statistics queries.

---

## Database Migration

### Flyway

Flyway is used for database schema management.

Reasons:

- Version controlled migrations
- Repeatable deployments
- Simple integration with Spring Boot

All database changes are managed through migration files.

---

# Local Development Setup

## Requirements

Install:

- Docker Desktop

---

## Start Application

Start the complete environment:

```bash
docker compose up
```

or for a fresh build:

```bash
docker compose up --build
```

---

## Stop Application

Stops the complete environment:

```bash
docker compose down -v
```