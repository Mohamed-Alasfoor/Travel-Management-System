package com.travelplan.users.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

public final class AuthContracts {
    private AuthContracts() { }
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) { }
    public record TokenResponse(String accessToken, String tokenType, Instant expiresAt) { }
}

