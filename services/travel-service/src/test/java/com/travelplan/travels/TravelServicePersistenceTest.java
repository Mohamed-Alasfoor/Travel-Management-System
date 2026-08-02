package com.travelplan.travels;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TravelServicePersistenceTest {

    @Autowired
    private TravelService travelService;

    @Autowired
    private TravelRepository travelRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void createPersistsTravelToTheRepository() {
        var created = travelService.create(new TravelContracts.CreateRequest(
                "Paris",
                "2026-09-01",
                5,
                "Museum",
                "Hotel",
                "Train"));

        assertThat(created.id()).isNotNull();
        assertThat(travelRepository.findById(created.id())).isPresent();
        assertThat(detailCount(created.id())).isEqualTo(4);

        travelService.delete(created.id());

        assertThat(travelRepository.findById(created.id())).isEmpty();
        assertThat(detailCount(created.id())).isZero();
    }

    private int detailCount(java.util.UUID travelId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM travel_details WHERE travel_id = ?", Integer.class, travelId);
    }
}
