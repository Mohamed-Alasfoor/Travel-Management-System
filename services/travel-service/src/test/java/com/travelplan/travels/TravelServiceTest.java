package com.travelplan.travels;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class TravelServiceTest {
    @Test
    void createsAndRetrievesTravels() {
        TravelRepository repository = mock(TravelRepository.class);
        when(repository.save(any(Travel.class))).thenAnswer(invocation -> invocation.getArgument(0));
        TravelService service = new TravelService(repository);

        TravelContracts.Response created = service.create(new TravelContracts.CreateRequest(
                "Paris",
                "2026-10-01 to 2026-10-10",
                9,
                "Museums, Seine cruise",
                "Hotel Rivoli",
                "Flight and metro"
        ));

        assertThat(created.destination()).isEqualTo("Paris");
        verify(repository).save(any(Travel.class));
    }

    @Test
    void updatesAndDeletesTravels() {
        TravelRepository repository = mock(TravelRepository.class);
        UUID id = UUID.randomUUID();
        Travel travel = new Travel("Paris", "2026-10-01", 3, "Museum", "Hotel", "Train");
        ReflectionTestUtils.setField(travel, "id", id);
        when(repository.findById(id)).thenReturn(Optional.of(travel));
        when(repository.save(travel)).thenReturn(travel);
        TravelService service = new TravelService(repository);

        var updated = service.update(id, new TravelContracts.UpdateRequest(
                "Lyon", "2026-10-02", 4, "Food tour", "Apartment", "Rail"));
        assertThat(updated.destination()).isEqualTo("Lyon");
        assertThat(updated.durationDays()).isEqualTo(4);

        service.delete(id);
        verify(repository).delete(travel);
    }
}
