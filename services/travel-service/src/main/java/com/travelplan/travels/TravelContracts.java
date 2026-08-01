package com.travelplan.travels;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public final class TravelContracts {
    private TravelContracts() { }

    public record CreateRequest(
            @NotBlank String destination,
            @NotBlank String dates,
            @NotNull @Min(1) Integer durationDays,
            @NotBlank String activities,
            @NotBlank String accommodation,
            @NotBlank String transportation) { }

    public record UpdateRequest(
            @NotBlank String destination,
            @NotBlank String dates,
            @NotNull @Min(1) Integer durationDays,
            @NotBlank String activities,
            @NotBlank String accommodation,
            @NotBlank String transportation) { }

    public record Response(UUID id, String destination, String dates, int durationDays,
                           String activities, String accommodation, String transportation,
                           Instant createdAt, Instant updatedAt) {
        static Response from(Travel travel) {
            return new Response(travel.getId(), travel.getDestination(), travel.getDates(),
                    travel.getDurationDays(), travel.getActivities(), travel.getAccommodation(),
                    travel.getTransportation(), travel.getCreatedAt(), travel.getUpdatedAt());
        }
    }
}
