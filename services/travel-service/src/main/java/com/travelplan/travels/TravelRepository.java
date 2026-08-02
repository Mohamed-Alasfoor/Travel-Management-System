package com.travelplan.travels;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface TravelRepository extends JpaRepository<Travel, UUID> {
  List<Travel> findByManagerIdOrderByStartDateDesc(UUID managerId);
}
