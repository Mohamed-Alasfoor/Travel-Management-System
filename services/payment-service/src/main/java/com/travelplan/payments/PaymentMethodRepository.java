package com.travelplan.payments;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface PaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {}
