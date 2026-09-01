package com.mcpgateway.admin.dto;

import java.time.Instant;
import java.util.UUID;

public record ToolContractResponse(
        UUID id,
        String toolName,
        String description,
        String inputSchema,
        String outputSchema,
        String source,
        Instant createdAt
) {}
