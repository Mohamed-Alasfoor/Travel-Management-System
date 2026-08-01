# Travel Plan Admin Platform

Java 21 microservices and an admin dashboard for managing users, travel itineraries, and Stripe/PayPal payment-method configuration.

## Architecture

- Spring Cloud Gateway (`8080`) is the only application ingress and hosts the responsive admin dashboard.
- User service (`8081`) owns accounts, BCrypt credentials, JWT login, roles, and bootstrap administration.
- Travel service (`8082`) owns destinations, dates, durations, activities, accommodation, and transportation.
- Payment service (`8083`) owns enabled Stripe/PayPal method configurations; provider secrets are never persisted.
- PostgreSQL uses a separate database and least-privilege user per service.
- Neo4j is isolated for the recommendation graph used by the next project phase.
- Prometheus collects metrics; Grafana (`3000`) visualizes Prometheus metrics and centralized Loki logs.
- Docker Compose runs two replicas of every business service. Docker DNS load-balances gateway requests across them.
- Jenkins, SonarQube, and Ansible provide CI quality gates and repeatable deployment.

See [the architecture decision](docs/architecture/0001-system-foundation.md) and [manual setup checklist](docs/MANUAL_SETUP.md).

## Local start

Requirements: Docker Desktop with Linux containers. Java/Maven are optional when using Docker for the build.

```powershell
Copy-Item .env.example .env
# Edit every value marked change-me before starting.
docker compose up --build --detach
docker compose ps
```

Open:

- Admin dashboard: http://localhost:8080
- Gateway health: http://localhost:8080/actuator/health
- Grafana: http://localhost:3000

Log in using `BOOTSTRAP_ADMIN_EMAIL` and `BOOTSTRAP_ADMIN_PASSWORD` from `.env`. The bootstrap account is created only if the configured address does not exist. API calls to users, travels, and payment methods require an `ADMIN` JWT at the gateway.

## Verification

With Maven installed:

```powershell
mvn --batch-mode clean verify
docker compose config --quiet
```

Without Maven:

```powershell
docker run --rm -v "${PWD}:/workspace" -w /workspace maven:3.9.11-eclipse-temurin-21 mvn --batch-mode clean verify
```

## Useful operations

```powershell
docker compose logs -f gateway user-service travel-service payment-service
docker compose down
```

Persistent database and monitoring data remain after `down`. Use `docker compose down --volumes` only when you intentionally want to erase all local project data.

## Library choices

- Spring Boot/Web/Data/Security: cohesive, well-supported REST, validation, persistence, JWT, and observability stack.
- Flyway: deterministic, reviewable database schema migrations.
- Spring Cloud Gateway: reactive routing and one authorization boundary.
- PostgreSQL and Neo4j: transactional ownership plus relationship-oriented recommendations.
- Prometheus/Grafana/Loki: metrics and centralized logs with one operations UI.
- Testcontainers are intentionally not required; H2-backed persistence tests keep PR builds fast and deterministic.
