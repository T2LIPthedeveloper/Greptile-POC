package com.mcpgateway.admin.dto;

import java.time.Instant;
import java.util.UUID;

public record CredentialResponse(
        UUID id,
        String name,
        String credentialType,
        int keyVersion,
        Instant expiresAt,
        Instant createdAt
) {}
