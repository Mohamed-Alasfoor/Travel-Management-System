package com.travelplan.travels;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "travels")
class Travel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String destination;

  @Column(nullable = false)
  private String dates;

  @Column(nullable = false)
  private int durationDays;

  @Column(nullable = false)
  private String activities;

  @Column(nullable = false)
  private String accommodation;

  @Column(nullable = false)
  private String transportation;

  @Column(name = "manager_id")
  private UUID managerId;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal price;

  @Column(nullable = false)
  private int capacity;

  @Column(nullable = false, length = 20)
  private String status;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  @OneToMany(mappedBy = "travel", cascade = CascadeType.ALL, orphanRemoval = true)
  private final List<TravelDetail> details = new ArrayList<>();

  protected Travel() {}

  Travel(
      String destination,
      String dates,
      int durationDays,
      String activities,
      String accommodation,
      String transportation) {
    this(
        destination,
        dates,
        durationDays,
        activities,
        accommodation,
        transportation,
        null,
        LocalDate.now().plusDays(30),
        LocalDate.now().plusDays(30 + durationDays),
        BigDecimal.ZERO,
        100);
  }

  Travel(
      String destination,
      String dates,
      int durationDays,
      String activities,
      String accommodation,
      String transportation,
      UUID managerId,
      LocalDate startDate,
      LocalDate endDate,
      BigDecimal price,
      int capacity) {
    this.destination = destination;
    this.dates = dates;
    this.durationDays = durationDays;
    this.activities = activities;
    this.accommodation = accommodation;
    this.transportation = transportation;
    this.managerId = managerId;
    this.startDate = startDate;
    this.endDate = endDate;
    this.price = price;
    this.capacity = capacity;
    this.status = "PUBLISHED";
    replaceDetails(destination, activities, accommodation, transportation);
    this.createdAt = Instant.now();
    this.updatedAt = this.createdAt;
  }

  UUID getId() {
    return id;
  }

  String getDestination() {
    return destination;
  }

  String getDates() {
    return dates;
  }

  int getDurationDays() {
    return durationDays;
  }

  String getActivities() {
    return activities;
  }

  String getAccommodation() {
    return accommodation;
  }

  String getTransportation() {
    return transportation;
  }

  UUID getManagerId() {
    return managerId;
  }

  LocalDate getStartDate() {
    return startDate;
  }

  LocalDate getEndDate() {
    return endDate;
  }

  BigDecimal getPrice() {
    return price;
  }

  int getCapacity() {
    return capacity;
  }

  String getStatus() {
    return status;
  }

  Instant getCreatedAt() {
    return createdAt;
  }

  Instant getUpdatedAt() {
    return updatedAt;
  }

  void update(
      String destination,
      String dates,
      int durationDays,
      String activities,
      String accommodation,
      String transportation) {
    this.destination = destination;
    this.dates = dates;
    this.durationDays = durationDays;
    this.activities = activities;
    this.accommodation = accommodation;
    this.transportation = transportation;
    replaceDetails(destination, activities, accommodation, transportation);
    this.updatedAt = Instant.now();
  }

  void updateOffering(
      String destination,
      String dates,
      int durationDays,
      String activities,
      String accommodation,
      String transportation,
      LocalDate startDate,
      LocalDate endDate,
      BigDecimal price,
      int capacity,
      String status) {
    update(destination, dates, durationDays, activities, accommodation, transportation);
    this.startDate = startDate;
    this.endDate = endDate;
    this.price = price;
    this.capacity = capacity;
    this.status = status;
  }

  private void replaceDetails(
      String destination, String activities, String accommodation, String transportation) {
    details.clear();
    addCommaSeparatedDetails(TravelDetail.Type.DESTINATION, destination);
    addCommaSeparatedDetails(TravelDetail.Type.ACTIVITY, activities);
    addCommaSeparatedDetails(TravelDetail.Type.ACCOMMODATION, accommodation);
    addCommaSeparatedDetails(TravelDetail.Type.TRANSPORTATION, transportation);
  }

  private void addCommaSeparatedDetails(TravelDetail.Type type, String values) {
    for (String value : values.split(",")) {
      String normalized = value.trim();
      if (!normalized.isEmpty()) details.add(new TravelDetail(this, type, normalized));
    }
  }
}
