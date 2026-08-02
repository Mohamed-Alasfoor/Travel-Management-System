package com.travelplan.payments;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_methods")
class PaymentMethod {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String provider;

  @Column(nullable = false)
  private boolean enabled;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  protected PaymentMethod() {}

  PaymentMethod(String name, String provider, boolean enabled) {
    this.name = name;
    this.provider = provider;
    this.enabled = enabled;
    this.createdAt = Instant.now();
    this.updatedAt = this.createdAt;
  }

  UUID getId() {
    return id;
  }

  String getName() {
    return name;
  }

  String getProvider() {
    return provider;
  }

  boolean isEnabled() {
    return enabled;
  }

  Instant getCreatedAt() {
    return createdAt;
  }

  Instant getUpdatedAt() {
    return updatedAt;
  }

  void update(String name, String provider, boolean enabled) {
    this.name = name;
    this.provider = provider;
    this.enabled = enabled;
    this.updatedAt = Instant.now();
  }
}
