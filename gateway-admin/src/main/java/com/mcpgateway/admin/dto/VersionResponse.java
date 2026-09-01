package com.mcpgateway.admin.dto;

import java.time.Instant;
import java.util.UUID;

public record VersionResponse(
        UUID id,
        UUID providerId,
        String versionLabel,
        String protocolVersion,
        String status,
        String changelog,
        Instant publishedAt,
        Instant createdAt
) {}
