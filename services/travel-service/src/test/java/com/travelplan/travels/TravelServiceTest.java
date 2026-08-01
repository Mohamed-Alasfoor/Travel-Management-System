package com.travelplan.travels;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

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
}
