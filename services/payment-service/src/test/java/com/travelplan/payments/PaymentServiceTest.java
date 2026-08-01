package com.travelplan.payments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

class PaymentServiceTest {
    @Test
    void managesPaymentMethods() {
        PaymentMethodRepository repository = mock(PaymentMethodRepository.class);
        when(repository.save(any(PaymentMethod.class))).thenAnswer(invocation -> invocation.getArgument(0));
        PaymentService service = new PaymentService(repository);

        PaymentContracts.Response created = service.create(new PaymentContracts.CreateRequest("Stripe", "card", true));

        assertThat(created.name()).isEqualTo("Stripe");
        assertThat(created.enabled()).isTrue();
        verify(repository).save(any(PaymentMethod.class));
    }
}
