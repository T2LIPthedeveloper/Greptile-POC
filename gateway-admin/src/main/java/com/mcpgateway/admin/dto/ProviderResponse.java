package com.mcpgateway.admin.dto;

import java.time.Instant;
import java.util.UUID;

public record ProviderResponse(
        UUID id,
        String slug,
        String displayName,
        String description,
        String status,
        Instant createdAt,
        Instant updatedAt
) {}
