package com.travelplan.payments;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
  private final PaymentMethodRepository paymentMethodRepository;

  public PaymentService(PaymentMethodRepository paymentMethodRepository) {
    this.paymentMethodRepository = paymentMethodRepository;
  }

  @Transactional(readOnly = true)
  public List<PaymentContracts.Response> findAll() {
    return paymentMethodRepository.findAll().stream()
        .sorted(Comparator.comparing(PaymentMethod::getCreatedAt).reversed())
        .map(PaymentContracts.Response::from)
        .toList();
  }

  @Transactional(readOnly = true)
  public PaymentContracts.Response find(UUID id) {
    return PaymentContracts.Response.from(requirePaymentMethod(id));
  }

  @Transactional
  public PaymentContracts.Response create(PaymentContracts.CreateRequest request) {
    PaymentMethod paymentMethod =
        new PaymentMethod(request.name().trim(), request.provider().trim(), request.enabled());
    return PaymentContracts.Response.from(paymentMethodRepository.save(paymentMethod));
  }

  @Transactional
  public PaymentContracts.Response update(UUID id, PaymentContracts.UpdateRequest request) {
    PaymentMethod paymentMethod = requirePaymentMethod(id);
    paymentMethod.update(request.name().trim(), request.provider().trim(), request.enabled());
    return PaymentContracts.Response.from(paymentMethodRepository.save(paymentMethod));
  }

  @Transactional
  public void delete(UUID id) {
    paymentMethodRepository.delete(requirePaymentMethod(id));
  }

  private PaymentMethod requirePaymentMethod(UUID id) {
    return paymentMethodRepository
        .findById(id)
        .orElseThrow(
            () -> new IllegalArgumentException("Payment method " + id + " does not exist."));
  }
}
