package com.travelplan.engagement;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reports")
class TravelReport {
  enum TargetType {
    TRAVEL,
    MANAGER,
    TRAVELER
  }

  enum Status {
    OPEN,
    REVIEWED,
    DISMISSED,
    RESOLVED
  }

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "reporter_id", nullable = false)
  private UUID reporterId;

  @Enumerated(EnumType.STRING)
  @Column(name = "target_type", nullable = false)
  private TargetType targetType;

  @Column(name = "target_id", nullable = false)
  private UUID targetId;

  @Column(name = "travel_id")
  private UUID travelId;

  @Column(nullable = false, length = 2000)
  private String reason;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected TravelReport() {}

  TravelReport(UUID reporterId, TargetType type, UUID targetId, UUID travelId, String reason) {
    this.reporterId = reporterId;
    targetType = type;
    this.targetId = targetId;
    this.travelId = travelId;
    this.reason = reason;
    status = Status.OPEN;
    createdAt = Instant.now();
  }

  void review(Status status) {
    this.status = status;
  }

  UUID id() {
    return id;
  }

  UUID reporterId() {
    return reporterId;
  }

  TargetType targetType() {
    return targetType;
  }

  UUID targetId() {
    return targetId;
  }

  UUID travelId() {
    return travelId;
  }

  String reason() {
    return reason;
  }

  Status status() {
    return status;
  }

  Instant createdAt() {
    return createdAt;
  }
}
