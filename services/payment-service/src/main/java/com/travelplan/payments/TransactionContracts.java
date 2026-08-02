package com.travelplan.payments;
import jakarta.validation.constraints.*; import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
final class TransactionContracts {private TransactionContracts(){}
 record ChargeRequest(@NotNull UUID travelId,@NotBlank @Pattern(regexp="STRIPE|PAYPAL")String provider,@NotNull @DecimalMin("0.00")BigDecimal amount,@NotBlank @Pattern(regexp="[A-Z]{3}")String currency,@NotBlank @Size(max=100)String idempotencyKey){}
 record Response(UUID id,UUID travelerId,UUID travelId,String provider,BigDecimal amount,String currency,String status,String providerReference,String idempotencyKey,Instant createdAt){static Response from(PaymentTransaction p){return new Response(p.id(),p.travelerId(),p.travelId(),p.provider(),p.amount(),p.currency(),p.status().name(),p.providerReference(),p.idempotencyKey(),p.createdAt());}}
}
