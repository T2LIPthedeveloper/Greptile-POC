package com.mcpgateway.admin.dto;

import java.time.Instant;
import java.util.UUID;

public record FederationPeerResponse(
        UUID id,
        String slug,
        String displayName,
        String peerUrl,
        String trustLevel,
        String status,
        String lastHealthStatus,
        Instant lastHealthAt
) {}
