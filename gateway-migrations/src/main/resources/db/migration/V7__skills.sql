CREATE TABLE skills (
    id UUID PRIMARY KEY,
    org_id UUID NOT NULL REFERENCES organizations(id),
    slug VARCHAR(64) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    description TEXT,
    definition JSON NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (org_id, slug)
);

CREATE TABLE skill_tool_bindings (
    id UUID PRIMARY KEY,
    skill_id UUID NOT NULL REFERENCES skills(id),
    tool_name VARCHAR(255) NOT NULL,
    version_id UUID NOT NULL REFERENCES mcp_versions(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
