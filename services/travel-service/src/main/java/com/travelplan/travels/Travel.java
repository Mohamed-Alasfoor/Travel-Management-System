package com.travelplan.travels;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
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
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;

    protected Travel() {
    }

    Travel(String destination, String dates, int durationDays, String activities,
           String accommodation, String transportation) {
        this.destination = destination;
        this.dates = dates;
        this.durationDays = durationDays;
        this.activities = activities;
        this.accommodation = accommodation;
        this.transportation = transportation;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    UUID getId() { return id; }
    String getDestination() { return destination; }
    String getDates() { return dates; }
    int getDurationDays() { return durationDays; }
    String getActivities() { return activities; }
    String getAccommodation() { return accommodation; }
    String getTransportation() { return transportation; }
    Instant getCreatedAt() { return createdAt; }
    Instant getUpdatedAt() { return updatedAt; }

    void update(String destination, String dates, int durationDays, String activities,
                String accommodation, String transportation) {
        this.destination = destination;
        this.dates = dates;
        this.durationDays = durationDays;
        this.activities = activities;
        this.accommodation = accommodation;
        this.transportation = transportation;
        this.updatedAt = Instant.now();
    }
}
