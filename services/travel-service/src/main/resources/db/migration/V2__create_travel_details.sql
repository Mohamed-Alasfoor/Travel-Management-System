CREATE TABLE travel_details (
    id UUID PRIMARY KEY,
    travel_id UUID NOT NULL,
    type VARCHAR(32) NOT NULL,
    detail_value VARCHAR(255) NOT NULL,
    CONSTRAINT fk_travel_details_travel
        FOREIGN KEY (travel_id) REFERENCES travels(id) ON DELETE CASCADE,
    CONSTRAINT ck_travel_details_type
        CHECK (type IN ('DESTINATION', 'ACTIVITY', 'ACCOMMODATION', 'TRANSPORTATION'))
);

CREATE INDEX idx_travel_details_travel_id ON travel_details(travel_id);

INSERT INTO travel_details (id, travel_id, type, detail_value)
SELECT gen_random_uuid(), id, 'DESTINATION', destination FROM travels;

INSERT INTO travel_details (id, travel_id, type, detail_value)
SELECT gen_random_uuid(), id, 'ACTIVITY', activities FROM travels;

INSERT INTO travel_details (id, travel_id, type, detail_value)
SELECT gen_random_uuid(), id, 'ACCOMMODATION', accommodation FROM travels;

INSERT INTO travel_details (id, travel_id, type, detail_value)
SELECT gen_random_uuid(), id, 'TRANSPORTATION', transportation FROM travels;
