package com.travelplan.users.user;

import com.travelplan.users.shared.ConflictException;
import com.travelplan.users.shared.ResourceNotFoundException;
import com.travelplan.users.user.UserContracts.CreateRequest;
import com.travelplan.users.user.UserContracts.PasswordRequest;
import com.travelplan.users.user.UserContracts.Response;
import com.travelplan.users.user.UserContracts.UpdateRequest;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Response> findAll() {
        return repository.findAll(Sort.by("createdAt").descending()).stream().map(Response::from).toList();
    }

    @Transactional(readOnly = true)
    public Response find(UUID id) { return Response.from(requireUser(id)); }

    @Transactional
    public Response create(CreateRequest request) {
        String email = normalize(request.email());
        if (repository.existsByEmailIgnoreCase(email)) throw new ConflictException("A user with this email already exists.");
        User user = new User(email, passwordEncoder.encode(request.password()), request.displayName().trim(),
                request.role(), request.enabled());
        return Response.from(repository.save(user));
    }

    @Transactional
    public Response update(UUID id, UpdateRequest request, UUID actorId) {
        User user = requireUser(id);
        String email = normalize(request.email());
        if (repository.existsByEmailIgnoreCaseAndIdNot(email, id)) throw new ConflictException("A user with this email already exists.");
        if (id.equals(actorId) && (!request.enabled() || request.role() != Role.ADMIN))
            throw new ConflictException("Administrators cannot disable or demote their own account.");
        ensureAdminRemains(user, request.role(), request.enabled());
        user.update(email, request.displayName().trim(), request.role(), request.enabled());
        return Response.from(user);
    }

    @Transactional
    public void changePassword(UUID id, PasswordRequest request) {
        requireUser(id).changePassword(passwordEncoder.encode(request.password()));
    }

    @Transactional
    public void delete(UUID id, UUID actorId) {
        if (id.equals(actorId)) throw new ConflictException("Administrators cannot delete their own account.");
        User user = requireUser(id);
        ensureAdminRemains(user, null, false);
        repository.delete(user);
    }

    private void ensureAdminRemains(User current, Role nextRole, boolean nextEnabled) {
        boolean removingActiveAdmin = current.getRole() == Role.ADMIN && current.isEnabled()
                && (nextRole != Role.ADMIN || !nextEnabled);
        if (removingActiveAdmin && repository.countByRoleAndEnabledTrue(Role.ADMIN) <= 1)
            throw new ConflictException("The final enabled administrator cannot be removed or disabled.");
    }

    private User requireUser(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User " + id + " does not exist."));
    }

    private String normalize(String email) { return email.trim().toLowerCase(Locale.ROOT); }
}

