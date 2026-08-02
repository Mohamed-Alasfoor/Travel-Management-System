package com.travelplan.engagement;

import jakarta.servlet.http.HttpServletRequest; import jakarta.validation.Valid; import java.net.URI; import java.util.*;
import org.springframework.http.ResponseEntity; import org.springframework.security.access.AccessDeniedException; import org.springframework.security.core.annotation.AuthenticationPrincipal; import org.springframework.security.oauth2.jwt.Jwt; import org.springframework.web.bind.annotation.*;

@RestController
class EngagementController {
 private final EngagementService service; private final SearchService search; private final RecommendationService recommendations;
 EngagementController(EngagementService service,SearchService search,RecommendationService recommendations){this.service=service;this.search=search;this.recommendations=recommendations;}
 @PostMapping("/api/subscriptions") ResponseEntity<EngagementContracts.SubscriptionResponse> subscribe(@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody EngagementContracts.SubscribeRequest body,HttpServletRequest request){var created=service.subscribe(subject(jwt),body,bearer(request));return ResponseEntity.created(URI.create("/api/subscriptions/"+created.id())).body(created);}
 @GetMapping("/api/subscriptions/me") List<EngagementContracts.SubscriptionResponse> mine(@AuthenticationPrincipal Jwt jwt){return service.mine(subject(jwt));}
 @DeleteMapping("/api/subscriptions/{travelId}") ResponseEntity<Void> unsubscribe(@PathVariable UUID travelId,@AuthenticationPrincipal Jwt jwt,HttpServletRequest request){service.unsubscribe(travelId,subject(jwt),bearer(request),false);return ResponseEntity.noContent().build();}
 @GetMapping("/api/subscriptions/travel/{travelId}") List<EngagementContracts.SubscriptionResponse> subscribers(@PathVariable UUID travelId,@AuthenticationPrincipal Jwt jwt,HttpServletRequest request){requireManager(jwt);return service.subscribers(travelId,subject(jwt),isAdmin(jwt),bearer(request));}
 @DeleteMapping("/api/subscriptions/travel/{travelId}/traveler/{travelerId}") ResponseEntity<Void> remove(@PathVariable UUID travelId,@PathVariable UUID travelerId,@AuthenticationPrincipal Jwt jwt,HttpServletRequest request){requireManager(jwt);service.removeSubscriber(travelId,travelerId,subject(jwt),isAdmin(jwt),bearer(request));return ResponseEntity.noContent().build();}
 @PostMapping("/api/feedback") ResponseEntity<EngagementContracts.FeedbackResponse> feedback(@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody EngagementContracts.FeedbackRequest body,HttpServletRequest request){var created=service.addFeedback(subject(jwt),body,bearer(request));return ResponseEntity.created(URI.create("/api/feedback/"+created.id())).body(created);}
 @GetMapping("/api/feedback/manager/{managerId}") List<EngagementContracts.FeedbackResponse> managerFeedback(@PathVariable UUID managerId,@AuthenticationPrincipal Jwt jwt){return service.feedbackForManager(managerId,subject(jwt),isAdmin(jwt));}
 @PostMapping("/api/reports") ResponseEntity<EngagementContracts.ReportResponse> report(@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody EngagementContracts.ReportRequest body){var created=service.report(subject(jwt),body);return ResponseEntity.created(URI.create("/api/reports/"+created.id())).body(created);}
 @GetMapping("/api/reports") List<EngagementContracts.ReportResponse> reports(@AuthenticationPrincipal Jwt jwt){requireAdmin(jwt);return service.allReports();}
 @PutMapping("/api/reports/{id}/status") ResponseEntity<Void> review(@PathVariable UUID id,@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody EngagementContracts.StatusRequest body){requireAdmin(jwt);service.reviewReport(id,body.status());return ResponseEntity.noContent().build();}
 @GetMapping("/api/statistics/me") EngagementContracts.TravelerStats travelerStats(@AuthenticationPrincipal Jwt jwt,HttpServletRequest request){return service.travelerStats(subject(jwt),bearer(request));}
 @GetMapping("/api/statistics/managers/{managerId}") EngagementContracts.ManagerStats managerStats(@PathVariable UUID managerId,HttpServletRequest request){return service.managerStats(managerId,bearer(request));}
 @GetMapping("/api/rankings/managers") List<EngagementContracts.ManagerStats> rankings(@AuthenticationPrincipal Jwt jwt,HttpServletRequest request){requireAdmin(jwt);return service.rankings(bearer(request));}
 @GetMapping("/api/rankings/travels") List<EngagementContracts.TravelPerformance> travelRankings(@AuthenticationPrincipal Jwt jwt){requireAdmin(jwt);return service.travelRankings();}
 @GetMapping("/api/statistics/income/monthly") List<EngagementContracts.MonthlyIncome> monthlyIncome(@AuthenticationPrincipal Jwt jwt){requireAdmin(jwt);return service.monthlyIncome();}
 @GetMapping("/api/subscriptions/history") List<EngagementContracts.SubscriptionResponse> history(@AuthenticationPrincipal Jwt jwt){requireAdmin(jwt);return service.history();}
 @GetMapping("/api/feedback") List<EngagementContracts.FeedbackResponse> allFeedback(@AuthenticationPrincipal Jwt jwt){requireAdmin(jwt);return service.allFeedback();}
 @GetMapping("/api/search/travels") List<TravelClient.TravelView> search(@RequestParam(defaultValue="")String q,HttpServletRequest request){return search.search(q,bearer(request));}
 @GetMapping("/api/search/autocomplete") List<String> autocomplete(@RequestParam String q,HttpServletRequest request){return search.autocomplete(q,bearer(request));}
 @GetMapping("/api/recommendations") List<UUID> recommend(@AuthenticationPrincipal Jwt jwt){return recommendations.recommend(subject(jwt));}
 private UUID subject(Jwt jwt){return UUID.fromString(jwt.getSubject());} private String bearer(HttpServletRequest r){return r.getHeader("Authorization");}
 private boolean isAdmin(Jwt jwt){List<String>r=jwt.getClaimAsStringList("roles");return r!=null&&r.contains("ADMIN");}
 private void requireAdmin(Jwt jwt){if(!isAdmin(jwt))throw new AccessDeniedException("Administrator access required.");}
 private void requireManager(Jwt jwt){List<String>r=jwt.getClaimAsStringList("roles");if(r==null||(!r.contains("ADMIN")&&!r.contains("TRAVEL_MANAGER")))throw new AccessDeniedException("Manager access required.");}
}
