CREATE TABLE subscriptions (
 id UUID PRIMARY KEY, travel_id UUID NOT NULL, traveler_id UUID NOT NULL, manager_id UUID,
 status VARCHAR(20) NOT NULL, payment_provider VARCHAR(20) NOT NULL, amount NUMERIC(12,2) NOT NULL,
 subscribed_at TIMESTAMP WITH TIME ZONE NOT NULL, cancelled_at TIMESTAMP WITH TIME ZONE,
 CONSTRAINT uk_subscription UNIQUE(travel_id, traveler_id),
 CONSTRAINT ck_subscription_status CHECK(status IN ('ACTIVE','CANCELLED')),
 CONSTRAINT ck_subscription_provider CHECK(payment_provider IN ('STRIPE','PAYPAL'))
);
CREATE INDEX idx_subscription_traveler ON subscriptions(traveler_id);
CREATE INDEX idx_subscription_manager ON subscriptions(manager_id);
CREATE TABLE feedback (
 id UUID PRIMARY KEY, travel_id UUID NOT NULL, traveler_id UUID NOT NULL, manager_id UUID,
 rating INT NOT NULL CHECK(rating BETWEEN 1 AND 5), comment VARCHAR(2000) NOT NULL,
 created_at TIMESTAMP WITH TIME ZONE NOT NULL, CONSTRAINT uk_feedback UNIQUE(travel_id, traveler_id)
);
CREATE TABLE reports (
 id UUID PRIMARY KEY, reporter_id UUID NOT NULL, target_type VARCHAR(20) NOT NULL,
 target_id UUID NOT NULL, travel_id UUID, reason VARCHAR(2000) NOT NULL,
 status VARCHAR(20) NOT NULL, created_at TIMESTAMP WITH TIME ZONE NOT NULL,
 CONSTRAINT ck_report_target CHECK(target_type IN ('TRAVEL','MANAGER','TRAVELER')),
 CONSTRAINT ck_report_status CHECK(status IN ('OPEN','REVIEWED','DISMISSED','RESOLVED'))
);
