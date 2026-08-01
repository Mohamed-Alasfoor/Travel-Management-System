# ADR 0001: System foundation

Status: Accepted

## Context

The platform needs independently scalable user, itinerary, and payment capabilities, an admin-facing API, relational and graph storage, observable workloads, and automated deployment.

## Decision

Use Java 21, Spring Boot, Maven, and independently deployable services behind Spring Cloud Gateway. Each transactional service receives a separate PostgreSQL database and database user. Neo4j stores recommendation relationships rather than transactional records. All workloads emit health and Prometheus metrics.

Local orchestration uses Docker Compose. Production provisioning is expressed with Ansible; a later deployment target may replace Compose with Kubernetes without changing service boundaries.

Authentication is centralized in `user-service`, which issues short-lived JWT access tokens. The gateway validates tokens and enforces the admin role. Downstream services and their databases are attached only to the internal network, so the gateway is their sole ingress. Provider secrets must come from Vault and must never be stored in application tables or Git.

## Consequences

- Services can scale and fail independently.
- Cross-service database joins are forbidden; workflows use APIs or events.
- Cascades are limited to aggregates owned by one service. Cross-service deletion requires an explicit workflow and audit trail.
- Compose replicas cannot use fixed host ports; only the gateway is published to the host in production-like profiles.
