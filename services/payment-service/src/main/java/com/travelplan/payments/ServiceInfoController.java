package com.travelplan.payments;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment-methods")
class ServiceInfoController {
  @GetMapping("/status")
  Map<String, String> status() {
    return Map.of("service", "payment-service", "status", "available");
  }
}
