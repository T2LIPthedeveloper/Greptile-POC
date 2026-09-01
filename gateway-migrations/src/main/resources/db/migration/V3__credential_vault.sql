CREATE TABLE credential_vault (
    id UUID PRIMARY KEY,
    org_id UUID NOT NULL REFERENCES organizations(id),
    name VARCHAR(255) NOT NULL,
    credential_type VARCHAR(32) NOT NULL,
    encrypted_payload VARBINARY(8192) NOT NULL,
    key_version INT NOT NULL DEFAULT 1,
    expires_at TIMESTAMP,
    rotated_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE version_credentials (
    version_id UUID NOT NULL REFERENCES mcp_versions(id),
    credential_id UUID NOT NULL REFERENCES credential_vault(id),
    usage VARCHAR(32) NOT NULL,
    PRIMARY KEY (version_id, credential_id, usage)
);

CREATE INDEX idx_credentials_org ON credential_vault(org_id);
