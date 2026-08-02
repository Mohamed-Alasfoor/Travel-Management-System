# Let's Travel

Java 21 microservices and a role-aware travel platform for administrators, travel managers, and travelers.

## Architecture

- Spring Cloud Gateway (`8080`) is the only application ingress and hosts the responsive role-aware UI.
- User service (`8081`) owns accounts, BCrypt credentials, JWT login, and `ADMIN`, `TRAVEL_MANAGER`, and `TRAVELER` roles.
- Travel service (`8082`) owns manager offerings, structured dates, prices, capacity, lifecycle status, and itinerary details.
- Payment service (`8083`) owns Stripe/PayPal configuration and idempotent transaction records; card or wallet credentials are never persisted.
- Engagement service (`8084`) owns subscriptions, the three-day cutoff, feedback, reports, rankings, statistics, search, and recommendations.
- PostgreSQL uses a separate database and least-privilege user per service.
- Elasticsearch provides dynamic multi-field search and autocomplete.
- Neo4j stores participation/rating relationships and recommends travels using destination, activities, and accommodation similarity.
- Prometheus collects metrics; Grafana (`3000`) visualizes Prometheus metrics and centralized Loki logs.
- Docker Compose runs two replicas of every business service. Docker DNS load-balances gateway requests across them.
- HashiCorp Vault supplies application and database secrets in the local environment; production uses a non-development Vault auth method.
- Jenkins, SonarQube, and Ansible provide CI quality gates and repeatable deployment.

See the [system foundation](docs/architecture/0001-system-foundation.md) and [role-aware platform](docs/architecture/0002-role-aware-platform.md) decisions for the design and trade-offs.

## Local start

Requirements: Docker Desktop with Linux containers. Java/Maven are optional when using Docker for the build.

```powershell
Copy-Item .env.example .env
# Edit every value marked change-me before starting.
docker compose up --build --detach
docker compose ps
```

Open:

- Let's Travel application: http://localhost:8080
- Gateway health: http://localhost:8080/actuator/health
- Grafana: http://localhost:3000

Log in using `BOOTSTRAP_ADMIN_EMAIL` and `BOOTSTRAP_ADMIN_PASSWORD` from `.env`. Administrators can create manager and traveler accounts from the UI. Every service validates JWT roles independently as defense in depth.

Local payments use the Stripe/PayPal sandbox boundary and generate provider references without collecting sensitive card or wallet data. Production provider keys belong in Vault using the variables documented in `.env.example`; a hosted provider checkout/tokenization flow should supply payment tokens so PCI-sensitive fields never pass through this application.

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

## Local CI

Start the isolated Jenkins and SonarQube stack with:

```powershell
docker compose -f compose.ci.yml up --build --detach
```

Jenkins is available at `http://localhost:8085` and SonarQube at `http://localhost:9000`. GitHub push webhooks can reach the local Jenkins endpoint through a temporary ngrok tunnel to port `8085`.

For production HTTPS, set `DOMAIN` to a public DNS name and deploy with both Compose files. Caddy obtains and renews Let's Encrypt certificates automatically:

```shell
docker compose -f compose.yml -f compose.tls.yml up --build --detach
```

## Useful operations

```powershell
docker compose logs -f gateway user-service travel-service payment-service engagement-service elasticsearch neo4j
docker compose down
```

Persistent database and monitoring data remain after `down`. Use `docker compose down --volumes` only when you intentionally want to erase all local project data.

## Library choices

- Spring Boot/Web/Data/Security: cohesive, well-supported REST, validation, persistence, JWT, and observability stack.
- Flyway: deterministic, reviewable database schema migrations.
- Spring Cloud Gateway: reactive routing and one authorization boundary.
- PostgreSQL, Elasticsearch, and Neo4j: transactional ownership, full-text discovery, and relationship-oriented recommendations.
- Prometheus/Grafana/Loki: metrics and centralized logs with one operations UI.
- H2-backed migration/integration tests and Mockito unit tests keep CI deterministic; the acceptance workflow additionally exercises the live container topology.

## Security and privacy

- TLS is terminated by Caddy in production and HSTS/security headers are enabled.
- JWTs are short-lived, BCrypt uses strength 12, RBAC is enforced at both gateway and service boundaries, and databases are internal-only.
- Payment idempotency prevents accidental duplicate charges; only provider references and transaction metadata are stored.
- `.env` is ignored, application secrets are loaded from Vault, and logs must not include JWTs or payment credentials.
- Profile access is available through `/api/users/me`; administrators can delete accounts, while engagement records retain pseudonymous UUIDs for financial/audit obligations. Production retention and erasure periods must be configured to match the deploying jurisdiction.
