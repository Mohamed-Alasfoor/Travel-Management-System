package com.travelplan.payments;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment-methods")
public class PaymentController {
  private final PaymentService service;

  public PaymentController(PaymentService service) {
    this.service = service;
  }

  @GetMapping
  public List<PaymentContracts.Response> findAll() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  public PaymentContracts.Response find(@PathVariable UUID id) {
    return service.find(id);
  }

  @PostMapping
  public ResponseEntity<PaymentContracts.Response> create(
      @Valid @RequestBody PaymentContracts.CreateRequest request) {
    PaymentContracts.Response created = service.create(request);
    return ResponseEntity.created(URI.create("/api/payment-methods/" + created.id())).body(created);
  }

  @PutMapping("/{id}")
  public PaymentContracts.Response update(
      @PathVariable UUID id, @Valid @RequestBody PaymentContracts.UpdateRequest request) {
    return service.update(id, request);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
