package com.travelplan.users.auth;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
class AuthController {
    private final AuthService service;
    AuthController(AuthService service) { this.service = service; }

    @PostMapping("/login")
    AuthContracts.TokenResponse login(@Valid @RequestBody AuthContracts.LoginRequest request) { return service.login(request); }
}

