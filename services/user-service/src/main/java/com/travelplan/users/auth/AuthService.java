package com.travelplan.users.auth;

import com.travelplan.users.user.User;
import com.travelplan.users.user.UserRepository;
import java.util.Locale;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository repository;
    private final TokenService tokens;

    AuthService(AuthenticationManager authenticationManager, UserRepository repository, TokenService tokens) {
        this.authenticationManager = authenticationManager;
        this.repository = repository;
        this.tokens = tokens;
    }

    AuthContracts.TokenResponse login(AuthContracts.LoginRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(email, request.password()));
        } catch (AuthenticationException exception) {
            throw new BadCredentialsException("Invalid email or password.");
        }
        User user = repository.findByEmailIgnoreCase(email).orElseThrow(() -> new BadCredentialsException("Invalid email or password."));
        return tokens.issue(user);
    }
}

