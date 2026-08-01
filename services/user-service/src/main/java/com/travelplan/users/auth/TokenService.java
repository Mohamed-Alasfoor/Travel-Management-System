package com.travelplan.users.auth;

import com.travelplan.users.user.User;
import java.time.Duration;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
class TokenService {
    private final JwtEncoder encoder;
    private final Duration ttl;

    TokenService(JwtEncoder encoder, @Value("${app.security.access-token-ttl}") Duration ttl) {
        this.encoder = encoder;
        this.ttl = ttl;
    }

    AuthContracts.TokenResponse issue(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(ttl);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("travel-plan-user-service")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("name", user.getDisplayName())
                .claim("roles", java.util.List.of(user.getRole().name()))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return new AuthContracts.TokenResponse(encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue(), "Bearer", expiresAt);
    }
}
