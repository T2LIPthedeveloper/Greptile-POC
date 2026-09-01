CREATE TABLE mcp_providers (
    id UUID PRIMARY KEY,
    org_id UUID NOT NULL REFERENCES organizations(id),
    slug VARCHAR(64) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    description TEXT,
    owner_user_id UUID REFERENCES users(id),
    status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (org_id, slug)
);

CREATE TABLE mcp_versions (
    id UUID PRIMARY KEY,
    provider_id UUID NOT NULL REFERENCES mcp_providers(id),
    version_label VARCHAR(64) NOT NULL,
    protocol_version VARCHAR(32) NOT NULL DEFAULT '2025-11-25',
    status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
    changelog TEXT,
    published_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (provider_id, version_label)
);

CREATE TABLE mcp_endpoints (
    id UUID PRIMARY KEY,
    version_id UUID NOT NULL REFERENCES mcp_versions(id),
    transport VARCHAR(32) NOT NULL,
    base_url TEXT NOT NULL,
    health_check_path VARCHAR(255) DEFAULT '/health',
    timeout_ms INT NOT NULL DEFAULT 30000,
    is_primary BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE mcp_tool_contracts (
    id UUID PRIMARY KEY,
    version_id UUID NOT NULL REFERENCES mcp_versions(id),
    tool_name VARCHAR(255) NOT NULL,
    description TEXT,
    input_schema JSON NOT NULL DEFAULT '{}',
    output_schema JSON,
    annotations JSON,
    source VARCHAR(32) NOT NULL DEFAULT 'MANUAL',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (version_id, tool_name)
);

CREATE TABLE mcp_resources (
    id UUID PRIMARY KEY,
    version_id UUID NOT NULL REFERENCES mcp_versions(id),
    uri_template VARCHAR(1024) NOT NULL,
    name VARCHAR(255),
    mime_type VARCHAR(128),
    metadata JSON,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE mcp_prompts (
    id UUID PRIMARY KEY,
    version_id UUID NOT NULL REFERENCES mcp_versions(id),
    prompt_name VARCHAR(255) NOT NULL,
    description TEXT,
    arguments_schema JSON,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_providers_org_status ON mcp_providers(org_id, status);
CREATE INDEX idx_versions_provider_status ON mcp_versions(provider_id, status);
