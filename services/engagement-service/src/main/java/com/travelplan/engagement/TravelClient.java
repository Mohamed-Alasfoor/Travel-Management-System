package com.travelplan.engagement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
class TravelClient {
  record TravelView(
      UUID id,
      UUID managerId,
      LocalDate startDate,
      LocalDate endDate,
      BigDecimal price,
      int capacity,
      String status,
      String destination,
      String activities,
      String accommodation,
      String transportation) {}

  private final RestClient client;

  TravelClient(@Value("${app.travel-service-url}") String url) {
    client = RestClient.builder().baseUrl(url).build();
  }

  TravelView find(UUID id, String token) {
    return client
        .get()
        .uri("/api/travels/{id}", id)
        .header(HttpHeaders.AUTHORIZATION, token)
        .retrieve()
        .body(TravelView.class);
  }

  TravelView[] all(String token) {
    return client
        .get()
        .uri("/api/travels")
        .header(HttpHeaders.AUTHORIZATION, token)
        .retrieve()
        .body(TravelView[].class);
  }
}
