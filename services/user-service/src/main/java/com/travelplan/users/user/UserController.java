package com.travelplan.users.user;

import com.travelplan.users.user.UserContracts.CreateRequest;
import com.travelplan.users.user.UserContracts.PasswordRequest;
import com.travelplan.users.user.UserContracts.Response;
import com.travelplan.users.user.UserContracts.UpdateRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;
    public UserController(UserService service) { this.service = service; }

    @GetMapping public List<Response> findAll() { return service.findAll(); }
    @GetMapping("/{id}") public Response find(@PathVariable UUID id) { return service.find(id); }

    @PostMapping
    public ResponseEntity<Response> create(@Valid @RequestBody CreateRequest request) {
        Response created = service.create(request);
        return ResponseEntity.created(URI.create("/api/users/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public Response update(@PathVariable UUID id, @Valid @RequestBody UpdateRequest request, @org.springframework.security.core.annotation.AuthenticationPrincipal Jwt jwt) {
        return service.update(id, request, UUID.fromString(jwt.getSubject()));
    }

    @PutMapping("/{id}/password")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void changePassword(@PathVariable UUID id, @Valid @RequestBody PasswordRequest request) { service.changePassword(id, request); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @org.springframework.security.core.annotation.AuthenticationPrincipal Jwt jwt) {
        service.delete(id, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.noContent().build();
    }
}
