package com.travelplan.payments;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.Instant;
import java.util.UUID;

public final class PaymentContracts {
  private PaymentContracts() {}

  public record CreateRequest(
      @NotBlank String name,
      @NotBlank
          @Pattern(regexp = "(?i)STRIPE|PAYPAL", message = "provider must be STRIPE or PAYPAL")
          String provider,
      @NotNull Boolean enabled) {}

  public record UpdateRequest(
      @NotBlank String name,
      @NotBlank
          @Pattern(regexp = "(?i)STRIPE|PAYPAL", message = "provider must be STRIPE or PAYPAL")
          String provider,
      @NotNull Boolean enabled) {}

  public record Response(
      UUID id,
      String name,
      String provider,
      boolean enabled,
      Instant createdAt,
      Instant updatedAt) {
    static Response from(PaymentMethod paymentMethod) {
      return new Response(
          paymentMethod.getId(),
          paymentMethod.getName(),
          paymentMethod.getProvider(),
          paymentMethod.isEnabled(),
          paymentMethod.getCreatedAt(),
          paymentMethod.getUpdatedAt());
    }
  }
}
