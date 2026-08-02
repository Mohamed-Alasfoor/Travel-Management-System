# ADR 0002: Role-aware engagement platform

Status: Accepted

## Context

The second release adds administrator, travel-manager, and traveler experiences; transactional booking rules; searchable travel discovery; recommendations; feedback; reports; and performance analytics. These features must not weaken the service boundaries or expose payment credentials.

## Decision

Keep account and itinerary ownership in the existing user and travel services. Add an engagement service that owns subscriptions, feedback, reports, statistics, and the integrations with Elasticsearch and Neo4j. The payment service owns idempotent transaction metadata and supports the enabled Stripe and PayPal sandbox boundaries without accepting card or wallet credentials.

The gateway and every downstream service validate the same short-lived JWT. Managers may mutate only offerings they own. Traveler cancellation and manager removal use the same three-day cutoff. Feedback requires completed participation. Administrators receive aggregate rankings, income history, reports, feedback, and travel history.

Elasticsearch indexes destination, activities, accommodation, and transportation for autocomplete and multi-field search. Neo4j stores participation relationships and scores unseen travels using destination, activities, and accommodation similarity. PostgreSQL remains the source of truth.

## Consequences

- Search and recommendations can evolve independently of transactional schemas.
- A search outage falls back to the travel API; it cannot block booking data.
- Duplicate payments are prevented by an idempotency key.
- Payment credentials remain on hosted provider pages in production and never enter this system.
- Cross-service deletion retains pseudonymous engagement/payment records where audit or financial retention is required.
