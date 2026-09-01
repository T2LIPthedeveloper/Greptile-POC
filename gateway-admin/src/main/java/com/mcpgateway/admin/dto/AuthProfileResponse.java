package com.mcpgateway.admin.dto;

import java.time.Instant;
import java.util.UUID;

public record AuthProfileResponse(
        UUID id,
        String name,
        String authMethod,
        String config,
        UUID credentialId,
        Instant createdAt) {}
