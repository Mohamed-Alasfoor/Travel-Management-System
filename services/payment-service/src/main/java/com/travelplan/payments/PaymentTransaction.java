package com.travelplan.payments;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_transactions")
class PaymentTransaction {
  enum Status {
    SUCCEEDED,
    FAILED,
    REFUNDED
  }

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "traveler_id", nullable = false)
  private UUID travelerId;

  @Column(name = "travel_id", nullable = false)
  private UUID travelId;

  @Column(nullable = false)
  private String provider;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false, length = 3)
  private String currency;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;

  @Column(name = "provider_reference", nullable = false)
  private String providerReference;

  @Column(name = "idempotency_key", nullable = false, unique = true)
  private String idempotencyKey;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected PaymentTransaction() {}

  PaymentTransaction(
      UUID travelerId,
      UUID travelId,
      String provider,
      BigDecimal amount,
      String currency,
      String key,
      String reference) {
    this.travelerId = travelerId;
    this.travelId = travelId;
    this.provider = provider;
    this.amount = amount;
    this.currency = currency;
    idempotencyKey = key;
    providerReference = reference;
    status = Status.SUCCEEDED;
    createdAt = Instant.now();
  }

  UUID id() {
    return id;
  }

  UUID travelerId() {
    return travelerId;
  }

  UUID travelId() {
    return travelId;
  }

  String provider() {
    return provider;
  }

  BigDecimal amount() {
    return amount;
  }

  String currency() {
    return currency;
  }

  Status status() {
    return status;
  }

  String providerReference() {
    return providerReference;
  }

  String idempotencyKey() {
    return idempotencyKey;
  }

  Instant createdAt() {
    return createdAt;
  }
}
