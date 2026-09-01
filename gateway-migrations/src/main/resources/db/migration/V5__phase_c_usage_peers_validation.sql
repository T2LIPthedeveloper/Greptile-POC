CREATE TABLE usage_events (
    id UUID PRIMARY KEY,
    org_id UUID NOT NULL REFERENCES organizations(id),
    event_type VARCHAR(64) NOT NULL,
    resource_type VARCHAR(64),
    resource_id UUID,
    quantity BIGINT NOT NULL DEFAULT 1,
    metadata JSON,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_usage_org_time ON usage_events(org_id, created_at DESC);

CREATE TABLE federation_peers (
    id UUID PRIMARY KEY,
    org_id UUID NOT NULL REFERENCES organizations(id),
    slug VARCHAR(64) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    peer_url TEXT NOT NULL,
    trust_level VARCHAR(32) NOT NULL DEFAULT 'READ_ONLY',
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    last_health_status VARCHAR(32),
    last_health_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (org_id, slug)
);

ALTER TABLE mcp_endpoints ADD COLUMN last_health_status VARCHAR(32);
ALTER TABLE mcp_endpoints ADD COLUMN last_health_at TIMESTAMP;

CREATE TABLE contract_validation_runs (
    id UUID PRIMARY KEY,
    version_id UUID NOT NULL REFERENCES mcp_versions(id),
    valid BOOLEAN NOT NULL,
    errors JSON,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
