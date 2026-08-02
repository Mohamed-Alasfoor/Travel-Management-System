ALTER TABLE travels ADD COLUMN manager_id UUID;
ALTER TABLE travels ADD COLUMN start_date DATE;
ALTER TABLE travels ADD COLUMN end_date DATE;
ALTER TABLE travels ADD COLUMN price NUMERIC(12,2) NOT NULL DEFAULT 0;
ALTER TABLE travels ADD COLUMN capacity INT NOT NULL DEFAULT 100;
ALTER TABLE travels ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED';

UPDATE travels
SET start_date = CURRENT_DATE + 30,
    end_date = CURRENT_DATE + 30 + duration_days
WHERE start_date IS NULL;

ALTER TABLE travels ALTER COLUMN start_date SET NOT NULL;
ALTER TABLE travels ALTER COLUMN end_date SET NOT NULL;
ALTER TABLE travels ADD CONSTRAINT ck_travels_dates CHECK (end_date >= start_date);
ALTER TABLE travels ADD CONSTRAINT ck_travels_price CHECK (price >= 0);
ALTER TABLE travels ADD CONSTRAINT ck_travels_capacity CHECK (capacity > 0);
ALTER TABLE travels ADD CONSTRAINT ck_travels_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'CANCELLED', 'COMPLETED'));
CREATE INDEX idx_travels_manager ON travels(manager_id);
CREATE INDEX idx_travels_start_date ON travels(start_date);
