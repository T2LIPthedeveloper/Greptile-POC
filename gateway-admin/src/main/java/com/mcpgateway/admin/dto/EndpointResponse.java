package com.mcpgateway.admin.dto;

import java.util.UUID;

public record EndpointResponse(
        UUID id,
        String transport,
        String baseUrl,
        String healthCheckPath,
        int timeoutMs,
        boolean primary
) {}
