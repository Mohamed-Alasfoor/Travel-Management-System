package com.travelplan.users.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

public final class UserContracts {
    private UserContracts() { }

    public record CreateRequest(
            @NotBlank @Email @Size(max = 254) String email,
            @NotBlank @Size(min = 2, max = 100) String displayName,
            @NotBlank @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{12,72}$",
                    message = "must be 12-72 characters and contain upper, lower, number, and symbol") String password,
            @NotNull Role role,
            @NotNull Boolean enabled) { }

    public record UpdateRequest(
            @NotBlank @Email @Size(max = 254) String email,
            @NotBlank @Size(min = 2, max = 100) String displayName,
            @NotNull Role role,
            @NotNull Boolean enabled) { }

    public record PasswordRequest(
            @NotBlank @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{12,72}$",
                    message = "must be 12-72 characters and contain upper, lower, number, and symbol") String password) { }

    public record Response(UUID id, String email, String displayName, Role role, boolean enabled,
                           Instant createdAt, Instant updatedAt) {
        static Response from(User user) {
            return new Response(user.getId(), user.getEmail(), user.getDisplayName(), user.getRole(),
                    user.isEnabled(), user.getCreatedAt(), user.getUpdatedAt());
        }
    }

    public record PublicProfile(UUID id, String displayName, Role role) {
        static PublicProfile from(Response user) {
            return new PublicProfile(user.id(), user.displayName(), user.role());
        }
    }
}
