package com.travelplan.travels;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TravelService {
    private final TravelRepository travelRepository;

    public TravelService(TravelRepository travelRepository) {
        this.travelRepository = travelRepository;
    }

    @Transactional(readOnly = true)
    public List<TravelContracts.Response> findAll() {
        return travelRepository.findAll().stream()
                .sorted(Comparator.comparing(Travel::getCreatedAt).reversed())
                .map(TravelContracts.Response::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TravelContracts.Response find(UUID id) {
        return TravelContracts.Response.from(requireTravel(id));
    }

    @Transactional
    public TravelContracts.Response create(TravelContracts.CreateRequest request) {
        return create(request, null);
    }

    @Transactional
    public TravelContracts.Response create(TravelContracts.CreateRequest request, UUID managerId) {
        LocalDate start = request.startDate() == null ? LocalDate.now().plusDays(30) : request.startDate();
        LocalDate end = request.endDate() == null ? start.plusDays(request.durationDays()) : request.endDate();
        BigDecimal price = request.price() == null ? BigDecimal.ZERO : request.price();
        int capacity = request.capacity() == null ? 100 : request.capacity();
        validateOffering(start, end, price, capacity);
        Travel travel = new Travel(request.destination().trim(), request.dates().trim(),
                request.durationDays(), request.activities().trim(), request.accommodation().trim(),
                request.transportation().trim(), managerId, start, end, price, capacity);
        return TravelContracts.Response.from(travelRepository.save(travel));
    }

    @Transactional
    public TravelContracts.Response update(UUID id, TravelContracts.UpdateRequest request) {
        return update(id, request, null, true);
    }

    @Transactional
    public TravelContracts.Response update(UUID id, TravelContracts.UpdateRequest request, UUID actorId, boolean admin) {
        Travel travel = requireTravel(id);
        requireManagerOrAdmin(travel, actorId, admin);
        LocalDate start = request.startDate() == null ? travel.getStartDate() : request.startDate();
        LocalDate end = request.endDate() == null ? travel.getEndDate() : request.endDate();
        BigDecimal price = request.price() == null ? travel.getPrice() : request.price();
        int capacity = request.capacity() == null ? travel.getCapacity() : request.capacity();
        validateOffering(start, end, price, capacity);
        travel.updateOffering(request.destination().trim(), request.dates().trim(), request.durationDays(),
                request.activities().trim(), request.accommodation().trim(), request.transportation().trim(),
                start, end, price, capacity, request.status() == null ? travel.getStatus() : request.status());
        return TravelContracts.Response.from(travelRepository.save(travel));
    }

    @Transactional
    public void delete(UUID id) {
        travelRepository.delete(requireTravel(id));
    }

    @Transactional
    public void delete(UUID id, UUID actorId, boolean admin) {
        Travel travel = requireTravel(id);
        requireManagerOrAdmin(travel, actorId, admin);
        travelRepository.delete(travel);
    }

    @Transactional(readOnly = true)
    public List<TravelContracts.Response> findByManager(UUID managerId) {
        return travelRepository.findByManagerIdOrderByStartDateDesc(managerId).stream()
                .map(TravelContracts.Response::from).toList();
    }

    private void requireManagerOrAdmin(Travel travel, UUID actorId, boolean admin) {
        if (!admin && (actorId == null || !actorId.equals(travel.getManagerId()))) {
            throw new org.springframework.security.access.AccessDeniedException("Only the organizing manager can modify this travel.");
        }
    }

    private void validateOffering(LocalDate start, LocalDate end, BigDecimal price, int capacity) {
        if (end.isBefore(start)) throw new IllegalArgumentException("End date must not precede start date.");
        if (price.signum() < 0) throw new IllegalArgumentException("Price cannot be negative.");
        if (capacity < 1) throw new IllegalArgumentException("Capacity must be positive.");
    }

    private Travel requireTravel(UUID id) {
        return travelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Travel " + id + " does not exist."));
    }
}
