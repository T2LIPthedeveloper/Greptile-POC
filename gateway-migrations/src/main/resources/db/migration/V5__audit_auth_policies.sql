CREATE TABLE audit_events (
    id UUID PRIMARY KEY,
    org_id UUID NOT NULL,
    actor_type VARCHAR(32) NOT NULL,
    actor_id VARCHAR(255),
    action VARCHAR(64) NOT NULL,
    resource_type VARCHAR(64),
    resource_id UUID,
    metadata JSON,
    ip_address VARCHAR(64),
    correlation_id VARCHAR(64),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_org_time ON audit_events(org_id, created_at DESC);

CREATE TABLE auth_profiles (
    id UUID PRIMARY KEY,
    org_id UUID NOT NULL REFERENCES organizations(id),
    name VARCHAR(255) NOT NULL,
    auth_method VARCHAR(32) NOT NULL,
    config JSON NOT NULL DEFAULT '{}',
    credential_id UUID REFERENCES credential_vault(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE access_policies (
    id UUID PRIMARY KEY,
    subscription_id UUID NOT NULL REFERENCES consumer_subscriptions(id),
    policy_type VARCHAR(32) NOT NULL,
    policy_config JSON NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
