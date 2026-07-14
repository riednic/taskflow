# TaskFlow Workflow Engine

TaskFlow is a workflow management platform that allows users to create, manage, and process
tasks through a predefined workflow state machine.

The application provides:

- Task lifecycle management with a validated workflow state machine
- Role-based access control (`ADMIN`, `REVIEWER`, `MEMBER`)
- JWT-based authentication, with a Redis-backed logout/token-blocklist
- Optimistic locking for concurrent updates
- Audit logging of workflow transitions
- Asynchronous notifications to reviewers
- A performant statistics/reporting endpoint

The goal of this project is to demonstrate a production-oriented backend architecture using
Kotlin and Spring Boot.

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Project Structure](#project-structure)
- [Local Development Setup](#local-development-setup)
- [Initial Admin Seeding](#initial-admin-seeding)
- [Technology Stack & Design Decisions](#technology-stack--design-decisions)
- [Asynchronous Notifications](#asynchronous-notifications)
- [Testing](#testing)
- [Known Limitations](#known-limitations)

---

# Architecture Overview

The application follows a layered architecture with a clear separation between presentation,
business logic, and persistence, applied consistently per feature (`auth`, `user`, `task`,
`statistics`). Cross-cutting concerns (error handling, result types) live in `common`.

```
                                HTTP Client
                                    │
                                    │  Authorization: Bearer <JWT>
                                    ▼
        ┌──────────────────────────────────────────────────────────┐
        │ Spring Security Filter Chain                              │
        │  JwtAuthorizationFilter                                   │
        │   1. verify JWT signature + expiry (jjwt)                 │
        │   2. reject if token is on the Redis logout blocklist     │
        │   3. populate SecurityContext for downstream @PreAuthorize│
        └──────────────────────────┬─────────────────────────────--─┘
                                    ▼
        ┌──────────────────────────────────────────────────────────┐
        │ Controller (Presentation)                                 │
        │  auth / user / task / statistics                          │
        │  request validation, @PreAuthorize role checks             │
        └──────────────────────────┬─────────────────────────────--─┘
                                    ▼
        ┌──────────────────────────────────────────────────────────┐
        │ Service (Application / Business Logic)                    │
        │  - workflow state machine + role policy                   │
        │    (TaskStatusTransitionPolicy)                            │
        │  - optimistic locking (version check -> 409 Conflict)      │
        │  - publishes TaskEnteredReviewEvent on -> IN_REVIEW         │
        └───────┬──────────────────────────────────────┬───────────-┘
                 ▼                                       ▼ (async, after commit)
   ┌───────────────────────────┐          ┌──────────────────────────────────┐
   │ Repository (Persistence)  │          │ TaskReviewNotificationListener    │
   │  Spring Data JPA / native │          │  @Async @TransactionalEventListener│
   │  queries -> PostgreSQL    │          │  logs a notification per REVIEWER │
   │  (schema via Flyway)      │          └──────────────────────────────────┘
   └───────────────────────────┘
```

Redis is used in two places: as the logout token blocklist (checked by
`JwtAuthorizationFilter` on every request) and as nothing else currently — see
[Known Limitations](#known-limitations) for caching that was considered but not implemented.

---

# Project Structure

Each business feature is organized top-down the same way (`controller` → `application` →
`domain` → `persistence`), so any feature can be understood in isolation:

```
de.riednic.taskflow
├── auth          # login/logout, JWT issuance & validation, Redis token blocklist
├── user          # user CRUD, roles, initial admin seeding
├── task          # task CRUD, workflow state machine, transition audit log
├── statistics    # aggregated task/user statistics endpoint
└── common        # shared ServiceResult/RepositoryResult, global error handling
```

Within a feature:

- `controller` — REST endpoints, request/response DTOs, input validation
- `application` — services, use-case orchestration, repository interfaces (ports)
- `domain` — entities/value objects with business rules (e.g. the workflow state machine)
- `persistence` — JPA entities, Spring Data repositories, repository implementations (adapters)

---

# Local Development Setup

## Requirements

Install:

- Docker Desktop
- JDK 21 (only needed if you want to run Gradle tasks outside Docker)

## Configure Environment

Copy the example env file and adjust values if needed (defaults work out of the box):

```bash
cp .env.example .env
```

Make sure to change `ADMIN_PASSWORD` from its placeholder value — it becomes the initial admin
account's password on first startup (see [Initial Admin Seeding](#initial-admin-seeding)).

## Start Application

Start the complete environment (app + PostgreSQL + Redis):

```bash
docker compose up
```

Start in the background (detached):

```bash
docker compose up -d
```

Force a fresh image build, e.g. after dependency or code changes:

```bash
docker compose up --build
```

This gets you a running system in well under 5 minutes on a machine that already has Docker
Desktop installed.

## Check Status & Logs

```bash
docker compose ps
docker compose logs -f app
docker compose logs -f db
docker compose logs -f redis
```

## Stop Application

Stop containers but keep the database volume:

```bash
docker compose down
```

Stop containers and wipe the database volume (clean slate — the next start will re-seed the
initial admin):

```bash
docker compose down -v
```

## Access the Database

Open a `psql` shell inside the running `db` container:

```bash
docker compose exec db psql -U taskflow -d taskflow
```

## Run Tests

Tests use [Testcontainers](https://testcontainers.com) to spin up real PostgreSQL and Redis
instances automatically — Docker must be running, but the containers do **not** need to be
started via `docker compose` first:

```bash
./gradlew test
```

## Run/Build Without Docker Compose

Useful while iterating on code without rebuilding the image each time.

Start only the dependencies:

```bash
docker compose up db redis -d
```

Run the app against them directly via Gradle (override `DB_HOST`/`REDIS_HOST` since the app now
runs on the host, not inside the Docker network):

```bash
DB_HOST=localhost REDIS_HOST=localhost ./gradlew bootRun
```

Build a runnable jar:

```bash
./gradlew bootJar
java -jar build/libs/taskflow-0.0.1-SNAPSHOT.jar
```

---

# Initial Admin Seeding

The first `ADMIN` user is created automatically at application startup by `AdminUserSeeder`
(`user/application/AdminUserSeeder.kt`), a Spring `ApplicationRunner` that runs once Flyway
migrations have completed.

On every startup it checks whether a user with role `ADMIN` already exists:

- If **no** admin exists yet, it creates one from the `ADMIN_NAME`, `ADMIN_EMAIL` and
  `ADMIN_PASSWORD` environment variables (see `.env.example`), hashing the password with the
  same `BCryptPasswordEncoder` used for regular user registration.
- If an admin **already** exists, it does nothing.

This makes seeding idempotent: restarts and redeploys never duplicate or reset the admin
account, even though the runner executes on every boot.

Credentials are configured exclusively via environment variables — never hardcoded, never
committed. Set `ADMIN_NAME` / `ADMIN_EMAIL` / `ADMIN_PASSWORD` in your `.env` (or the
deployment's secret store) before the first start; `ADMIN_PASSWORD` in particular should be
changed from the `.env.example` placeholder.

## Why not seed via a Flyway migration?

Postgres' `pgcrypto` extension can compute a real BCrypt hash directly in SQL
(`crypt(password, gen_salt('bf'))`), so a migration-based seed was considered. It was rejected
because Flyway placeholder substitution is a naive string replace, not a parameterized query — a
password containing a `'` would break out of the SQL string literal (or worse). Seeding via
application code instead reuses the exact same validated path as regular user creation (`NewUser`
validation, `BCryptPasswordEncoder`, unique-email constraint handling) with no string-interpolated
secrets.

---

# Technology Stack & Design Decisions

## Framework: Spring Boot

Alternatives considered:

**Ktor** — Kotlin-native, lightweight, flexible, but requires more manual setup and less
built-in enterprise functionality (security, validation, DI all need extra libraries wired up
by hand).

**Quarkus** — fast startup times, cloud-native focus, good container support, but a smaller
ecosystem and less widespread enterprise usage than Spring.

Spring Boot was chosen because it provides dependency injection, security integration, database
integration, validation, and testing support out of the box, backed by a mature ecosystem — all
directly relevant to this challenge's requirements (RBAC, JWT, optimistic locking, migrations).

## Database: PostgreSQL

Alternatives considered: MySQL, MariaDB, H2 (in-memory).

PostgreSQL was chosen for its maturity, strong consistency guarantees, and excellent Hibernate
support. H2 would have been sufficient for the challenge's scope, but Postgres was preferred to
exercise Postgres-specific features actually used here (named enum types, `citext` for
case-insensitive email uniqueness, `pgcrypto`-style reasoning during the admin-seeding design,
native aggregate SQL for the statistics endpoint) — closer to a realistic production setup.

## ORM: JPA / Hibernate

Reduces boilerplate SQL, integrates with Spring Data, provides transaction management, and
supports optimistic locking via `@Version` directly. The statistics endpoint bypasses JPQL in
favor of native SQL where Postgres-specific aggregate functions are needed (see
`StatisticsRepository`) — Hibernate is used where it earns its keep, not dogmatically.

## Migrations: Flyway

Version-controlled, repeatable, integrates with Spring Boot with no extra setup. All schema
changes are plain SQL migration files under `src/main/resources/db/migration`.

## Token Blocklist: Redis

Logout requires revoking a JWT before its natural expiry. Since JWTs are stateless by design,
revocation needs external state somewhere. An in-memory blocklist (a nice-to-have alternative
per the challenge) would not survive a restart and would not work across multiple app instances.
Redis was chosen over a database table for this because blocklist entries are inherently
short-lived (a key's TTL is set to the token's remaining lifetime, so Postgres would need either
a scheduled cleanup job for expired entries. Redis expires them automatically for free.

---

# Asynchronous Notifications

When a task transitions to `IN_REVIEW`, all `REVIEWER` users should be notified. This is
implemented with Spring's own eventing, not a message broker:

- `TaskService` publishes a `TaskEnteredReviewEvent` via `ApplicationEventPublisher` as part of
  the transition.
- `TaskReviewNotificationListener` handles it with `@Async` (runs on a separate thread pool, so
  the transition HTTP response doesn't wait for notification dispatch) and
  `@TransactionalEventListener(phase = AFTER_COMMIT)` (so nothing fires if the transition's
  transaction rolls back, e.g. because of a version conflict).
- The notification is currently logged per reviewer (satisfies "logged or persisted to a table"
  from the challenge).

This was chosen over a real message broker (RabbitMQ/Redis Pub/Sub) because there is no separate
consumer service in this system that would need the durability, retry, or fan-out guarantees a
broker provides — introducing one would add operational complexity without a corresponding
benefit at this scope. The trade-off is real, though: an in-process event is lost if the app
crashes between commit and listener execution, with no retry. See
[Known Limitations](#known-limitations).

---

# Testing

**Unit tests:**

- Workflow state machine — all allowed and disallowed transitions
  (`task/domain/TaskStatusTransitionTest.kt`)
- Role-based transition policy (`task/domain/TaskTransitionPolicyTest.kt`)
- JWT validation — valid, expired, tampered, wrong-secret, wrong-subject tokens
  (`auth/application/JwtServiceTest.kt`)

**Integration tests:**

- Happy path: login → create task → transition status, against a real Postgres via
  Testcontainers (`TaskWorkflowIntegrationTest.kt`)

Run everything with:

```bash
./gradlew test
```

---

# Known Limitations

What's here works and is tested, but given more time these are the things worth revisiting:

- **Auth would move to a managed identity provider (e.g. Keycloak).** Hand-rolling JWT issuance,
  password hashing, and a Redis blocklist is a reasonable amount of security-critical code to
  own for a challenge, but a real production system benefits from delegating this to a
  battle-tested IdP — standardized token lifecycle (refresh, revocation, MFA), centralized user
  management, and less custom code that has to be trusted.
- **Static analysis (Detekt) is currently disabled**, not just unconfigured: `build.gradle.kts`
  explicitly turns the `Detekt` task off (`enabled = false`) with a `TODO` to re-enable it once
  outstanding findings are triaged (fixed, or the ruleset adjusted). Right now nothing enforces
  the project's own lint rules in the build.
- **Several optional (nice-to-have) items from the challenge were not implemented**, in
  particular: OpenAPI/Swagger documentation, a `POST /auth/refresh` token-refresh endpoint,
  RS256 (asymmetric) signing instead of HS256, a real message broker for notifications instead
  of the in-process event described above, a retry mechanism for failed notifications, and
  caching for the statistics endpoint.
- **No reverse proxy / load balancer in front of the app.** `docker compose` currently exposes
  a single app instance directly. A reverse proxy (e.g. nginx or Traefik) in front, load
  balancing across multiple app instances, would be needed before running more than one replica
  or terminating TLS at the edge.
- **`PATCH /tasks/{id}` cannot clear `description` or `assignedTo` to `null`.** Passing
  `"description": null` / `"assignedTo": null` in the request body is indistinguishable from
  omitting the field entirely, so the update is silently ignored instead of clearing the column
  (see the `TODO` in `task/controller/TaskController.kt`). Fixing this properly needs a way to
  tell "not provided" apart from "explicitly set to null" (e.g. `JsonNullable<T>` or a
  `PATCH`-specific wrapper type per field) rather than plain nullable Kotlin properties.
