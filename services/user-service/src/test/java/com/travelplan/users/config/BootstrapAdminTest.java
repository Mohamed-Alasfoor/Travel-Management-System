package com.travelplan.users.config;

import static org.mockito.Mockito.verifyNoInteractions;

import com.travelplan.users.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;

class BootstrapAdminTest {
    @Test
    void shouldNotBootstrapAdminWhenCredentialsAreMissing() {
        UserRepository repository = org.mockito.Mockito.mock(UserRepository.class);
        PasswordEncoder encoder = org.mockito.Mockito.mock(PasswordEncoder.class);
        BootstrapAdmin bootstrapAdmin = new BootstrapAdmin(repository, encoder, "", "", "System Administrator");

        bootstrapAdmin.run(new DefaultApplicationArguments(new String[0]));

        verifyNoInteractions(repository, encoder);
    }
}
