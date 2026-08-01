package com.travelplan.users.config;

import com.travelplan.users.user.Role;
import com.travelplan.users.user.User;
import com.travelplan.users.user.UserRepository;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
class BootstrapAdmin implements ApplicationRunner {
    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final String email;
    private final String password;
    private final String name;

    BootstrapAdmin(UserRepository repository, PasswordEncoder encoder,
                   @Value("${app.bootstrap-admin.email}") String email,
                   @Value("${app.bootstrap-admin.password}") String password,
                   @Value("${app.bootstrap-admin.name}") String name) {
        this.repository = repository;
        this.encoder = encoder;
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(password)) return;
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        if (!repository.existsByEmailIgnoreCase(normalized))
            repository.save(new User(normalized, encoder.encode(password), name.trim(), Role.ADMIN, true));
    }
}

