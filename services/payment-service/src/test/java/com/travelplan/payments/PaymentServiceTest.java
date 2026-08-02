package com.travelplan.payments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class PaymentServiceTest {
    @Test
    void managesPaymentMethods() {
        PaymentMethodRepository repository = mock(PaymentMethodRepository.class);
        when(repository.save(any(PaymentMethod.class))).thenAnswer(invocation -> invocation.getArgument(0));
        PaymentService service = new PaymentService(repository);

        PaymentContracts.Response created = service.create(new PaymentContracts.CreateRequest("Stripe", "STRIPE", true));

        assertThat(created.name()).isEqualTo("Stripe");
        assertThat(created.enabled()).isTrue();
        verify(repository).save(any(PaymentMethod.class));
    }

    @Test
    void updatesAndDeletesPaymentMethods() {
        PaymentMethodRepository repository = mock(PaymentMethodRepository.class);
        UUID id = UUID.randomUUID();
        PaymentMethod method = new PaymentMethod("PayPal", "PAYPAL", true);
        ReflectionTestUtils.setField(method, "id", id);
        when(repository.findById(id)).thenReturn(Optional.of(method));
        when(repository.save(method)).thenReturn(method);
        PaymentService service = new PaymentService(repository);

        var updated = service.update(id, new PaymentContracts.UpdateRequest("PayPal Checkout", "PAYPAL", false));
        assertThat(updated.name()).isEqualTo("PayPal Checkout");
        assertThat(updated.enabled()).isFalse();

        service.delete(id);
        verify(repository).delete(method);
    }
}
