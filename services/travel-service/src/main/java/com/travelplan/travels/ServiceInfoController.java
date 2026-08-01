package com.travelplan.travels;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/travels")
class ServiceInfoController {
    @GetMapping("/status")
    Map<String, String> status() { return Map.of("service", "travel-service", "status", "available"); }
}

