package com.travelplan.payments;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {
  Optional<PaymentTransaction> findByIdempotencyKey(String key);

  List<PaymentTransaction> findByTravelerIdOrderByCreatedAtDesc(UUID travelerId);
}
