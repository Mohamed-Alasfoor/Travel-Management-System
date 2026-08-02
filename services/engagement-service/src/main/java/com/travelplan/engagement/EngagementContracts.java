package com.travelplan.engagement;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

final class EngagementContracts {
  private EngagementContracts() {}

  record SubscribeRequest(
      @NotNull UUID travelId,
      @NotBlank @Pattern(regexp = "STRIPE|PAYPAL") String paymentProvider) {}

  record SubscriptionResponse(
      UUID id,
      UUID travelId,
      UUID travelerId,
      UUID managerId,
      String status,
      String paymentProvider,
      BigDecimal amount,
      Instant subscribedAt,
      Instant cancelledAt) {
    static SubscriptionResponse from(Subscription s) {
      return new SubscriptionResponse(
          s.id(),
          s.travelId(),
          s.travelerId(),
          s.managerId(),
          s.status().name(),
          s.provider(),
          s.amount(),
          s.subscribedAt(),
          s.cancelledAt());
    }
  }

  record FeedbackRequest(
      @NotNull UUID travelId,
      @Min(1) @Max(5) int rating,
      @NotBlank @Size(max = 2000) String comment) {}

  record FeedbackResponse(
      UUID id,
      UUID travelId,
      UUID travelerId,
      UUID managerId,
      int rating,
      String comment,
      Instant createdAt) {
    static FeedbackResponse from(Feedback f) {
      return new FeedbackResponse(
          f.id(),
          f.travelId(),
          f.travelerId(),
          f.managerId(),
          f.rating(),
          f.comment(),
          f.createdAt());
    }
  }

  record ReportRequest(
      @NotNull TravelReport.TargetType targetType,
      @NotNull UUID targetId,
      UUID travelId,
      @NotBlank @Size(max = 2000) String reason) {}

  record ReportResponse(
      UUID id,
      UUID reporterId,
      String targetType,
      UUID targetId,
      UUID travelId,
      String reason,
      String status,
      Instant createdAt) {
    static ReportResponse from(TravelReport r) {
      return new ReportResponse(
          r.id(),
          r.reporterId(),
          r.targetType().name(),
          r.targetId(),
          r.travelId(),
          r.reason(),
          r.status().name(),
          r.createdAt());
    }
  }

  record StatusRequest(@NotNull TravelReport.Status status) {}

  record TravelerStats(
      long subscriptions,
      long active,
      long pastTrips,
      long cancellations,
      long feedback,
      long reports,
      String preferredPaymentMethod) {}

  record ManagerStats(
      UUID managerId,
      long trips,
      long travelers,
      BigDecimal income,
      double averageRating,
      long reports) {}

  record TravelPerformance(
      UUID travelId,
      UUID managerId,
      long travelers,
      BigDecimal income,
      long feedbackCount,
      double averageRating) {}

  record MonthlyIncome(String month, BigDecimal income) {}
}
