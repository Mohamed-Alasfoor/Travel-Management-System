package com.travelplan.engagement;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.*;

class EngagementServiceTest {
  SubscriptionRepository subscriptions = mock(SubscriptionRepository.class);
  FeedbackRepository feedback = mock(FeedbackRepository.class);
  ReportRepository reports = mock(ReportRepository.class);
  TravelClient travels = mock(TravelClient.class);
  RecommendationService recommendations = mock(RecommendationService.class);
  EngagementService service =
      new EngagementService(subscriptions, feedback, reports, travels, recommendations);

  @Test
  void subscribesWithSupportedProviderBeforeCutoff() {
    UUID user = UUID.randomUUID(), travelId = UUID.randomUUID(), manager = UUID.randomUUID();
    var travel =
        new TravelClient.TravelView(
            travelId,
            manager,
            LocalDate.now().plusDays(10),
            LocalDate.now().plusDays(15),
            new BigDecimal("250"),
            20,
            "PUBLISHED",
            "Paris",
            "Museums",
            "Hotel",
            "Train");
    when(travels.find(travelId, "Bearer token")).thenReturn(travel);
    when(subscriptions.findByTravelIdAndTravelerId(travelId, user)).thenReturn(Optional.empty());
    when(subscriptions.save(any())).thenAnswer(i -> i.getArgument(0));
    var result =
        service.subscribe(
            user, new EngagementContracts.SubscribeRequest(travelId, "STRIPE"), "Bearer token");
    assertThat(result.amount()).isEqualByComparingTo("250");
    verify(recommendations).participated(user, travel);
  }

  @Test
  void enforcesThreeDayCutoff() {
    UUID id = UUID.randomUUID();
    when(travels.find(eq(id), anyString()))
        .thenReturn(
            new TravelClient.TravelView(
                id,
                UUID.randomUUID(),
                LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(5),
                BigDecimal.TEN,
                5,
                "PUBLISHED",
                "Rome",
                "Food",
                "Hotel",
                "Bus"));
    assertThatThrownBy(
            () ->
                service.subscribe(
                    UUID.randomUUID(),
                    new EngagementContracts.SubscribeRequest(id, "PAYPAL"),
                    "token"))
        .hasMessageContaining("three days");
  }

  @Test
  void calculatesTravelerStatistics() {
    UUID user = UUID.randomUUID();
    Subscription active =
        new Subscription(UUID.randomUUID(), user, UUID.randomUUID(), "PAYPAL", BigDecimal.TEN);
    Subscription cancelled =
        new Subscription(UUID.randomUUID(), user, UUID.randomUUID(), "STRIPE", BigDecimal.ONE);
    cancelled.cancel();
    when(subscriptions.findByTravelerIdOrderBySubscribedAtDesc(user))
        .thenReturn(List.of(active, cancelled));
    when(feedback.countByTravelerId(user)).thenReturn(1L);
    when(reports.findByReporterIdOrderByCreatedAtDesc(user)).thenReturn(List.of());
    var stats = service.travelerStats(user);
    assertThat(stats.subscriptions()).isEqualTo(2);
    assertThat(stats.cancellations()).isEqualTo(1);
  }

  @Test
  void activeParticipantCanLeaveFeedbackBeforeTravelEnds() {
    UUID user = UUID.randomUUID(), travelId = UUID.randomUUID(), manager = UUID.randomUUID();
    var travel =
        new TravelClient.TravelView(
            travelId,
            manager,
            LocalDate.now().plusDays(10),
            LocalDate.now().plusDays(15),
            BigDecimal.TEN,
            5,
            "PUBLISHED",
            "Rome",
            "Food",
            "Hotel",
            "Bus");
    Subscription subscription = new Subscription(travelId, user, manager, "PAYPAL", BigDecimal.TEN);
    when(travels.find(travelId, "token")).thenReturn(travel);
    when(subscriptions.findByTravelIdAndTravelerId(travelId, user))
        .thenReturn(Optional.of(subscription));
    when(feedback.existsByTravelIdAndTravelerId(travelId, user)).thenReturn(false);
    when(feedback.save(any())).thenAnswer(i -> i.getArgument(0));
    var result =
        service.addFeedback(
            user, new EngagementContracts.FeedbackRequest(travelId, 5, "Excellent"), "token");
    assertThat(result.rating()).isEqualTo(5);
    verify(recommendations).rated(user, travel, 5);
  }

  @Test
  void travelerCannotReportSelf() {
    UUID user = UUID.randomUUID();
    assertThatThrownBy(
            () ->
                service.report(
                    user,
                    new EngagementContracts.ReportRequest(
                        TravelReport.TargetType.TRAVELER, user, null, "reason")))
        .hasMessageContaining("yourself");
  }

  @Test
  void travelerCanCancelAndViewBookings() {
    UUID user = UUID.randomUUID(), travelId = UUID.randomUUID(), manager = UUID.randomUUID();
    var travel = futureTravel(travelId, manager);
    Subscription booking = new Subscription(travelId, user, manager, "STRIPE", BigDecimal.TEN);
    when(travels.find(travelId, "token")).thenReturn(travel);
    when(subscriptions.findByTravelIdAndTravelerId(travelId, user))
        .thenReturn(Optional.of(booking));
    when(subscriptions.findByTravelerIdOrderBySubscribedAtDesc(user)).thenReturn(List.of(booking));
    service.unsubscribe(travelId, user, "token", false);
    assertThat(booking.status()).isEqualTo(Subscription.Status.CANCELLED);
    assertThat(service.mine(user)).hasSize(1);
  }

  @Test
  void managerCanInspectAndRemoveSubscribers() {
    UUID manager = UUID.randomUUID(), traveler = UUID.randomUUID(), travelId = UUID.randomUUID();
    var travel = futureTravel(travelId, manager);
    Subscription booking = new Subscription(travelId, traveler, manager, "PAYPAL", BigDecimal.TEN);
    when(travels.find(travelId, "token")).thenReturn(travel);
    when(subscriptions.findByTravelIdAndStatus(travelId, Subscription.Status.ACTIVE))
        .thenReturn(List.of(booking));
    when(subscriptions.findByTravelIdAndTravelerId(travelId, traveler))
        .thenReturn(Optional.of(booking));
    assertThat(service.subscribers(travelId, manager, false, "token")).hasSize(1);
    service.removeSubscriber(travelId, traveler, manager, false, "token");
    assertThat(booking.status()).isEqualTo(Subscription.Status.CANCELLED);
  }

  @Test
  void adminAnalyticsAggregateSubscriptionsFeedbackAndReports() {
    UUID manager = UUID.randomUUID(), traveler = UUID.randomUUID(), travelId = UUID.randomUUID();
    Subscription booking =
        new Subscription(travelId, traveler, manager, "STRIPE", new BigDecimal("75"));
    Feedback review = new Feedback(travelId, traveler, manager, 4, "Good");
    TravelReport report =
        new TravelReport(traveler, TravelReport.TargetType.MANAGER, manager, travelId, "Late");
    when(subscriptions.findAll()).thenReturn(List.of(booking));
    when(subscriptions.findByManagerIdOrderBySubscribedAtDesc(manager))
        .thenReturn(List.of(booking));
    when(feedback.findAll()).thenReturn(List.of(review));
    when(feedback.findByTravelIdOrderByCreatedAtDesc(travelId)).thenReturn(List.of(review));
    when(feedback.findByManagerIdOrderByCreatedAtDesc(manager)).thenReturn(List.of(review));
    when(reports.findAll()).thenReturn(List.of(report));
    when(reports.findById(any())).thenReturn(Optional.of(report));
    when(reports.countByTargetId(manager)).thenReturn(1L);
    when(travels.all("token"))
        .thenReturn(new TravelClient.TravelView[] {futureTravel(travelId, manager)});
    assertThat(service.managerStats(manager, "token").income()).isEqualByComparingTo("75");
    assertThat(service.rankings("token")).hasSize(1);
    assertThat(service.travelRankings()).hasSize(1);
    assertThat(service.monthlyIncome()).hasSize(1);
    assertThat(service.history()).hasSize(1);
    assertThat(service.allFeedback()).hasSize(1);
    assertThat(service.allReports()).hasSize(1);
    service.reviewReport(UUID.randomUUID(), TravelReport.Status.RESOLVED);
    assertThat(report.status()).isEqualTo(TravelReport.Status.RESOLVED);
  }

  @Test
  void reportingFeedbackDashboardAndPastTripStatisticsWork() {
    UUID manager = UUID.randomUUID(), traveler = UUID.randomUUID(), travelId = UUID.randomUUID();
    Feedback review = new Feedback(travelId, traveler, manager, 5, "Excellent");
    when(feedback.findByManagerIdOrderByCreatedAtDesc(manager)).thenReturn(List.of(review));
    when(reports.save(any())).thenAnswer(i -> i.getArgument(0));
    Subscription booking = new Subscription(travelId, traveler, manager, "PAYPAL", BigDecimal.TEN);
    when(subscriptions.findByTravelerIdOrderBySubscribedAtDesc(traveler))
        .thenReturn(List.of(booking));
    when(reports.findByReporterIdOrderByCreatedAtDesc(traveler)).thenReturn(List.of());
    when(travels.all("token"))
        .thenReturn(
            new TravelClient.TravelView[] {
              new TravelClient.TravelView(
                  travelId,
                  manager,
                  LocalDate.now().minusDays(5),
                  LocalDate.now().minusDays(1),
                  BigDecimal.TEN,
                  5,
                  "COMPLETED",
                  "Rome",
                  "Food",
                  "Hotel",
                  "Bus")
            });
    assertThat(service.feedbackForManager(manager, manager, false)).hasSize(1);
    assertThat(
            service
                .report(
                    traveler,
                    new EngagementContracts.ReportRequest(
                        TravelReport.TargetType.MANAGER, manager, travelId, "Unsafe"))
                .reason())
        .isEqualTo("Unsafe");
    assertThat(service.travelerStats(traveler, "token").pastTrips()).isEqualTo(1);
  }

  private TravelClient.TravelView futureTravel(UUID id, UUID manager) {
    return new TravelClient.TravelView(
        id,
        manager,
        LocalDate.now().plusDays(10),
        LocalDate.now().plusDays(12),
        BigDecimal.TEN,
        10,
        "PUBLISHED",
        "Paris",
        "Museums",
        "Hotel",
        "Train");
  }
}
