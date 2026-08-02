package com.travelplan.engagement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
  Optional<Subscription> findByTravelIdAndTravelerId(UUID travelId, UUID travelerId);

  List<Subscription> findByTravelerIdOrderBySubscribedAtDesc(UUID travelerId);

  List<Subscription> findByManagerIdOrderBySubscribedAtDesc(UUID managerId);

  List<Subscription> findByTravelIdAndStatus(UUID travelId, Subscription.Status status);

  long countByTravelIdAndStatus(UUID travelId, Subscription.Status status);
}

interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
  boolean existsByTravelIdAndTravelerId(UUID travelId, UUID travelerId);

  List<Feedback> findByManagerIdOrderByCreatedAtDesc(UUID managerId);

  List<Feedback> findByTravelIdOrderByCreatedAtDesc(UUID travelId);

  long countByTravelerId(UUID travelerId);
}

interface ReportRepository extends JpaRepository<TravelReport, UUID> {
  List<TravelReport> findByReporterIdOrderByCreatedAtDesc(UUID reporterId);

  long countByTargetId(UUID targetId);
}
