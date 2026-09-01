CREATE TABLE consumers (
    id UUID PRIMARY KEY,
    org_id UUID NOT NULL REFERENCES organizations(id),
    slug VARCHAR(64) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (org_id, slug)
);

CREATE TABLE consumer_subscriptions (
    id UUID PRIMARY KEY,
    consumer_id UUID NOT NULL REFERENCES consumers(id),
    provider_id UUID NOT NULL REFERENCES mcp_providers(id),
    version_id UUID REFERENCES mcp_versions(id),
    gateway_path VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (consumer_id, provider_id)
);

CREATE TABLE gateway_sessions (
    id UUID PRIMARY KEY,
    subscription_id UUID NOT NULL REFERENCES consumer_subscriptions(id),
    mcp_session_id VARCHAR(255) NOT NULL,
    proxy_instance_id VARCHAR(64),
    upstream_session_id VARCHAR(255),
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    expires_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_gateway_sessions_mcp ON gateway_sessions(mcp_session_id);
