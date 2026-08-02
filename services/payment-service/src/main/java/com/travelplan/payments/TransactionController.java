package com.travelplan.payments;
import jakarta.validation.Valid; import java.net.URI; import java.util.*; import org.springframework.http.ResponseEntity; import org.springframework.security.access.AccessDeniedException; import org.springframework.security.core.annotation.AuthenticationPrincipal; import org.springframework.security.oauth2.jwt.Jwt; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/payments") class TransactionController {
 private final TransactionService service; TransactionController(TransactionService service){this.service=service;}
 @PostMapping ResponseEntity<TransactionContracts.Response> charge(@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody TransactionContracts.ChargeRequest request){var created=service.charge(UUID.fromString(jwt.getSubject()),request);return ResponseEntity.created(URI.create("/api/payments/"+created.id())).body(created);}
 @GetMapping("/me") List<TransactionContracts.Response> mine(@AuthenticationPrincipal Jwt jwt){return service.mine(UUID.fromString(jwt.getSubject()));}
 @GetMapping List<TransactionContracts.Response> all(@AuthenticationPrincipal Jwt jwt){List<String>roles=jwt.getClaimAsStringList("roles");if(roles==null||!roles.contains("ADMIN"))throw new AccessDeniedException("Administrator access required.");return service.all();}
}
