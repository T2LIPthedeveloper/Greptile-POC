ALTER TABLE mcp_providers ADD COLUMN provider_type VARCHAR(32) NOT NULL DEFAULT 'REMOTE_HTTP';

CREATE TABLE publish_approvals (
    id UUID PRIMARY KEY,
    version_id UUID NOT NULL REFERENCES mcp_versions(id),
    requested_by UUID NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    reviewed_by UUID,
    reviewed_at TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_publish_approvals_status ON publish_approvals(status, created_at DESC);
