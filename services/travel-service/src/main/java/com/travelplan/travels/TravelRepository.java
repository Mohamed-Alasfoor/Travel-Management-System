package com.travelplan.travels;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface TravelRepository extends JpaRepository<Travel, UUID> {
}
