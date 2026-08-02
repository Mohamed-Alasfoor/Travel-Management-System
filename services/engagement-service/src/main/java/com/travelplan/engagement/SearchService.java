package com.travelplan.engagement;

import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.*;
import tools.jackson.databind.node.*;

@Service
class SearchService {
  private final String elastic;
  private final TravelClient travels;
  private final ObjectMapper mapper;
  private final HttpClient http = HttpClient.newHttpClient();

  SearchService(
      @Value("${app.elasticsearch-url}") String elastic,
      TravelClient travels,
      ObjectMapper mapper) {
    this.elastic = elastic;
    this.travels = travels;
    this.mapper = mapper;
  }

  List<TravelClient.TravelView> search(String query, String token) {
    TravelClient.TravelView[] all = travels.all(token);
    index(all);
    if (query == null || query.isBlank()) return Arrays.asList(all);
    try {
      ObjectNode root = mapper.createObjectNode();
      ObjectNode mm = root.putObject("query").putObject("multi_match");
      mm.put("query", query);
      mm.put("type", "bool_prefix");
      ArrayNode fields = mm.putArray("fields");
      fields
          .add("destination")
          .add("destination._2gram")
          .add("destination._3gram")
          .add("activities")
          .add("accommodation")
          .add("transportation");
      root.put("size", 20);
      HttpRequest req =
          HttpRequest.newBuilder(URI.create(elastic + "/travels/_search"))
              .header("Content-Type", "application/json")
              .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(root)))
              .build();
      JsonNode hits =
          mapper
              .readTree(http.send(req, HttpResponse.BodyHandlers.ofString()).body())
              .path("hits")
              .path("hits");
      List<TravelClient.TravelView> result = new ArrayList<>();
      for (JsonNode hit : hits)
        result.add(mapper.treeToValue(hit.path("_source"), TravelClient.TravelView.class));
      return result;
    } catch (Exception e) {
      return Arrays.stream(all)
          .filter(
              t ->
                  contains(t.destination(), query)
                      || contains(t.activities(), query)
                      || contains(t.accommodation(), query)
                      || contains(t.transportation(), query))
          .toList();
    }
  }

  List<String> autocomplete(String query, String token) {
    return search(query, token).stream()
        .map(TravelClient.TravelView::destination)
        .distinct()
        .limit(8)
        .toList();
  }

  private boolean contains(String value, String q) {
    return value != null && value.toLowerCase(Locale.ROOT).contains(q.toLowerCase(Locale.ROOT));
  }

  private void index(TravelClient.TravelView[] values) {
    for (var t : values)
      try {
        HttpRequest req =
            HttpRequest.newBuilder(URI.create(elastic + "/travels/_doc/" + t.id()))
                .header("Content-Type", "application/json")
                .PUT(
                    HttpRequest.BodyPublishers.ofString(
                        mapper.writeValueAsString(t), StandardCharsets.UTF_8))
                .build();
        http.send(req, HttpResponse.BodyHandlers.discarding());
      } catch (Exception ignored) {
      }
  }
}
