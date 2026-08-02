package com.travelplan.travels;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "travel_details")
class TravelDetail {
    enum Type { DESTINATION, ACTIVITY, ACCOMMODATION, TRANSPORTATION }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "travel_id", nullable = false)
    private Travel travel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Type type;

    @Column(name = "detail_value", nullable = false)
    private String value;

    protected TravelDetail() { }

    TravelDetail(Travel travel, Type type, String value) {
        this.travel = travel;
        this.type = type;
        this.value = value;
    }
}
