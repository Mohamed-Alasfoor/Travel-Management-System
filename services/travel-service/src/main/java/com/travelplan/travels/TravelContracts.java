package com.travelplan.travels;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public final class TravelContracts {
  private TravelContracts() {}

  public record CreateRequest(
      @NotBlank String destination,
      @NotBlank String dates,
      @NotNull @Min(1) Integer durationDays,
      @NotBlank String activities,
      @NotBlank String accommodation,
      @NotBlank String transportation,
      LocalDate startDate,
      LocalDate endDate,
      @DecimalMin("0.00") BigDecimal price,
      @Min(1) Integer capacity) {
    public CreateRequest(
        String destination,
        String dates,
        Integer durationDays,
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
          null,
          null,
          null);
    }
  }

  public record UpdateRequest(
      @NotBlank String destination,
      @NotBlank String dates,
      @NotNull @Min(1) Integer durationDays,
      @NotBlank String activities,
      @NotBlank String accommodation,
      @NotBlank String transportation,
      LocalDate startDate,
      LocalDate endDate,
      @DecimalMin("0.00") BigDecimal price,
      @Min(1) Integer capacity,
      @Pattern(regexp = "DRAFT|PUBLISHED|CANCELLED|COMPLETED") String status) {
    public UpdateRequest(
        String destination,
        String dates,
        Integer durationDays,
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
          null,
          null,
          null,
          null);
    }
  }

  public record Response(
      UUID id,
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
      int capacity,
      String status,
      Instant createdAt,
      Instant updatedAt) {
    static Response from(Travel travel) {
      return new Response(
          travel.getId(),
          travel.getDestination(),
          travel.getDates(),
          travel.getDurationDays(),
          travel.getActivities(),
          travel.getAccommodation(),
          travel.getTransportation(),
          travel.getManagerId(),
          travel.getStartDate(),
          travel.getEndDate(),
          travel.getPrice(),
          travel.getCapacity(),
          travel.getStatus(),
          travel.getCreatedAt(),
          travel.getUpdatedAt());
    }
  }
}
