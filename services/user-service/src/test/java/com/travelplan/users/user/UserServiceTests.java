package com.travelplan.users.user;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.travelplan.users.shared.ConflictException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {
  @Mock UserRepository repository;
  @Mock PasswordEncoder encoder;
  private UserService service;

  @BeforeEach
  void setUp() {
    service = new UserService(repository, encoder);
  }

  @Test
  void administratorCannotDeleteOwnAccount() {
    UUID actorId = UUID.randomUUID();
    assertThatThrownBy(() -> service.delete(actorId, actorId))
        .isInstanceOf(ConflictException.class)
        .hasMessageContaining("own account");
    verify(repository, never()).delete(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void finalEnabledAdministratorCannotBeDeleted() {
    UUID targetId = UUID.randomUUID();
    User admin = user(targetId, Role.ADMIN, true);
    when(repository.findById(targetId)).thenReturn(Optional.of(admin));
    when(repository.countByRoleAndEnabledTrue(Role.ADMIN)).thenReturn(1L);
    UUID actorId = UUID.randomUUID();

    assertThatThrownBy(() -> service.delete(targetId, actorId))
        .isInstanceOf(ConflictException.class)
        .hasMessageContaining("final enabled administrator");
    verify(repository, never()).delete(admin);
  }

  @Test
  void duplicateEmailIsRejectedBeforePasswordIsStored() {
    UserContracts.CreateRequest request =
        new UserContracts.CreateRequest(
            "existing@example.com", "Existing User", "StrongPassword1!", Role.TRAVELER, true);
    when(repository.existsByEmailIgnoreCase("existing@example.com")).thenReturn(true);

    assertThatThrownBy(() -> service.create(request)).isInstanceOf(ConflictException.class);
    verify(encoder, never()).encode(org.mockito.ArgumentMatchers.anyString());
  }

  private User user(UUID id, Role role, boolean enabled) {
    User user = new User("admin@example.com", "hash", "Admin", role, enabled);
    ReflectionTestUtils.setField(user, "id", id);
    return user;
  }
}
