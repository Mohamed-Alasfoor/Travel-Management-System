package com.travelplan.engagement;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "subscriptions", uniqueConstraints = @UniqueConstraint(columnNames = {"travel_id", "traveler_id"}))
class Subscription {
    enum Status { ACTIVE, CANCELLED }
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name="travel_id", nullable=false) private UUID travelId;
    @Column(name="traveler_id", nullable=false) private UUID travelerId;
    @Column(name="manager_id") private UUID managerId;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private Status status;
    @Column(name="payment_provider", nullable=false) private String paymentProvider;
    @Column(nullable=false, precision=12, scale=2) private BigDecimal amount;
    @Column(name="subscribed_at", nullable=false) private Instant subscribedAt;
    @Column(name="cancelled_at") private Instant cancelledAt;
    protected Subscription() {}
    Subscription(UUID travelId, UUID travelerId, UUID managerId, String provider, BigDecimal amount) {
        this.travelId=travelId; this.travelerId=travelerId; this.managerId=managerId;
        this.paymentProvider=provider; this.amount=amount; this.status=Status.ACTIVE; this.subscribedAt=Instant.now();
    }
    void reactivate(String provider, BigDecimal amount) { status=Status.ACTIVE; paymentProvider=provider; this.amount=amount; subscribedAt=Instant.now(); cancelledAt=null; }
    void cancel() { status=Status.CANCELLED; cancelledAt=Instant.now(); }
    UUID id(){return id;} UUID travelId(){return travelId;} UUID travelerId(){return travelerId;} UUID managerId(){return managerId;}
    Status status(){return status;} String provider(){return paymentProvider;} BigDecimal amount(){return amount;}
    Instant subscribedAt(){return subscribedAt;} Instant cancelledAt(){return cancelledAt;}
}
