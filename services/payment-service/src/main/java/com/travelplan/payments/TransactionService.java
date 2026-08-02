package com.travelplan.payments;
import java.util.*; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
@Service class TransactionService {
 private final PaymentTransactionRepository transactions; private final PaymentMethodRepository methods;
 TransactionService(PaymentTransactionRepository transactions,PaymentMethodRepository methods){this.transactions=transactions;this.methods=methods;}
 @Transactional TransactionContracts.Response charge(UUID traveler,TransactionContracts.ChargeRequest request){return transactions.findByIdempotencyKey(request.idempotencyKey()).map(TransactionContracts.Response::from).orElseGet(()->{boolean enabled=methods.findAll().stream().anyMatch(m->m.isEnabled()&&m.getProvider().equalsIgnoreCase(request.provider()));if(!enabled)throw new IllegalStateException("Selected payment provider is disabled.");String reference=request.provider().toLowerCase(Locale.ROOT)+"_sandbox_"+UUID.randomUUID();PaymentTransaction saved=transactions.save(new PaymentTransaction(traveler,request.travelId(),request.provider(),request.amount(),request.currency(),request.idempotencyKey(),reference));return TransactionContracts.Response.from(saved);});}
 @Transactional(readOnly=true) List<TransactionContracts.Response> mine(UUID traveler){return transactions.findByTravelerIdOrderByCreatedAtDesc(traveler).stream().map(TransactionContracts.Response::from).toList();}
 @Transactional(readOnly=true) List<TransactionContracts.Response> all(){return transactions.findAll().stream().map(TransactionContracts.Response::from).toList();}
}
