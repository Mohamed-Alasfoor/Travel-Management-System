package com.travelplan.users.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.travelplan.users.user.Role;
import com.travelplan.users.user.User;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.util.ReflectionTestUtils;

class TokenServiceTests {
    @Test
    void issuedTokenCanBeDecodedWithRoleAndSubject() {
        SecretKey key = new SecretKeySpec(
                "test-secret-that-is-at-least-thirty-two-bytes-long".getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        TokenService service = new TokenService(new NimbusJwtEncoder(new ImmutableSecret<>(key)), Duration.ofMinutes(15));
        UUID userId = UUID.randomUUID();
        User user = new User("admin@example.com", "hash", "Admin", Role.ADMIN, true);
        ReflectionTestUtils.setField(user, "id", userId);

        AuthContracts.TokenResponse response = service.issue(user);
        var decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
        var jwt = decoder.decode(response.accessToken());

        assertThat(jwt.getSubject()).isEqualTo(userId.toString());
        assertThat(jwt.getClaimAsStringList("roles")).containsExactly("ADMIN");
        assertThat(response.tokenType()).isEqualTo("Bearer");
    }
}
