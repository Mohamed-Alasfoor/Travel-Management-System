package com.travelplan.payments;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PaymentServicePersistenceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Test
    void createPersistsPaymentMethodToTheRepository() {
        var created = paymentService.create(new PaymentContracts.CreateRequest(
                "Stripe Cards",
                "STRIPE",
                true));

        assertThat(created.id()).isNotNull();
        assertThat(paymentMethodRepository.findById(created.id())).isPresent();
    }
}
