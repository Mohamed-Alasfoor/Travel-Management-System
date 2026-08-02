package com.travelplan.travels;

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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/travels")
public class TravelController {
    private final TravelService service;

    public TravelController(TravelService service) {
        this.service = service;
    }

    @GetMapping
    public List<TravelContracts.Response> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public TravelContracts.Response find(@PathVariable UUID id) {
        return service.find(id);
    }

    @PostMapping
    public ResponseEntity<TravelContracts.Response> create(@Valid @RequestBody TravelContracts.CreateRequest request,
                                                            @AuthenticationPrincipal Jwt jwt) {
        TravelContracts.Response created = service.create(request, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.created(URI.create("/api/travels/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public TravelContracts.Response update(@PathVariable UUID id, @Valid @RequestBody TravelContracts.UpdateRequest request,
                                           @AuthenticationPrincipal Jwt jwt) {
        return service.update(id, request, UUID.fromString(jwt.getSubject()), isAdmin(jwt));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        service.delete(id, UUID.fromString(jwt.getSubject()), isAdmin(jwt));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/manager/me")
    public List<TravelContracts.Response> mine(@AuthenticationPrincipal Jwt jwt) {
        return service.findByManager(UUID.fromString(jwt.getSubject()));
    }

    private boolean isAdmin(Jwt jwt) {
        java.util.List<String> roles = jwt.getClaimAsStringList("roles");
        return roles != null && roles.contains("ADMIN");
    }
}
