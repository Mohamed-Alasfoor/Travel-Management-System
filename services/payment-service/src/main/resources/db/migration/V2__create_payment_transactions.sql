CREATE TABLE payment_transactions (
 id UUID PRIMARY KEY, traveler_id UUID NOT NULL, travel_id UUID NOT NULL,
 provider VARCHAR(20) NOT NULL, amount NUMERIC(12,2) NOT NULL, currency VARCHAR(3) NOT NULL,
 status VARCHAR(20) NOT NULL, provider_reference VARCHAR(255) NOT NULL,
 idempotency_key VARCHAR(100) NOT NULL UNIQUE, created_at TIMESTAMP WITH TIME ZONE NOT NULL,
 CONSTRAINT ck_transaction_provider CHECK(provider IN ('STRIPE','PAYPAL')),
 CONSTRAINT ck_transaction_status CHECK(status IN ('SUCCEEDED','FAILED','REFUNDED')),
 CONSTRAINT ck_transaction_amount CHECK(amount >= 0)
);
CREATE INDEX idx_transactions_traveler ON payment_transactions(traveler_id);
CREATE INDEX idx_transactions_travel ON payment_transactions(travel_id);
