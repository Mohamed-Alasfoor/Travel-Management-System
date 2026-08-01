CREATE TABLE IF NOT EXISTS travels (
    id UUID PRIMARY KEY,
    destination VARCHAR(255) NOT NULL,
    dates VARCHAR(255) NOT NULL,
    duration_days INT NOT NULL,
    activities VARCHAR(255) NOT NULL,
    accommodation VARCHAR(255) NOT NULL,
    transportation VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
