CREATE TABLE skill_invocation_ledger (
    id UUID PRIMARY KEY,
    skill_id UUID NOT NULL REFERENCES skills(id),
    idempotency_key VARCHAR(255) NOT NULL,
    deterministic_seed VARCHAR(64),
    input_hash VARCHAR(64),
    output_hash VARCHAR(64),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (skill_id, idempotency_key)
);

ALTER TABLE organizations ADD COLUMN data_residency VARCHAR(32) DEFAULT 'US';

CREATE TABLE marketplace_catalog_entries (
    id UUID PRIMARY KEY,
    org_id UUID NOT NULL REFERENCES organizations(id),
    provider_id UUID NOT NULL REFERENCES mcp_providers(id),
    public_slug VARCHAR(64) NOT NULL,
    listed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (public_slug)
);
