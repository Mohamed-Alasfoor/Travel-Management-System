package com.travelplan.travels;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
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
        Travel travel = new Travel(request.destination().trim(), request.dates().trim(),
                request.durationDays(), request.activities().trim(), request.accommodation().trim(),
                request.transportation().trim());
        return TravelContracts.Response.from(travelRepository.save(travel));
    }

    @Transactional
    public TravelContracts.Response update(UUID id, TravelContracts.UpdateRequest request) {
        Travel travel = requireTravel(id);
        travel.update(request.destination().trim(), request.dates().trim(), request.durationDays(),
                request.activities().trim(), request.accommodation().trim(), request.transportation().trim());
        return TravelContracts.Response.from(travelRepository.save(travel));
    }

    @Transactional
    public void delete(UUID id) {
        travelRepository.delete(requireTravel(id));
    }

    private Travel requireTravel(UUID id) {
        return travelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Travel " + id + " does not exist."));
    }
}
