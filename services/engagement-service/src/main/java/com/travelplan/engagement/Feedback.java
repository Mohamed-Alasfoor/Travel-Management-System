package com.travelplan.engagement;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "feedback",
    uniqueConstraints = @UniqueConstraint(columnNames = {"travel_id", "traveler_id"}))
class Feedback {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "travel_id", nullable = false)
  private UUID travelId;

  @Column(name = "traveler_id", nullable = false)
  private UUID travelerId;

  @Column(name = "manager_id")
  private UUID managerId;

  @Column(nullable = false)
  private int rating;

  @Column(nullable = false, length = 2000)
  private String comment;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected Feedback() {}

  Feedback(UUID travelId, UUID travelerId, UUID managerId, int rating, String comment) {
    this.travelId = travelId;
    this.travelerId = travelerId;
    this.managerId = managerId;
    this.rating = rating;
    this.comment = comment;
    createdAt = Instant.now();
  }

  UUID id() {
    return id;
  }

  UUID travelId() {
    return travelId;
  }

  UUID travelerId() {
    return travelerId;
  }

  UUID managerId() {
    return managerId;
  }

  int rating() {
    return rating;
  }

  String comment() {
    return comment;
  }

  Instant createdAt() {
    return createdAt;
  }
}
