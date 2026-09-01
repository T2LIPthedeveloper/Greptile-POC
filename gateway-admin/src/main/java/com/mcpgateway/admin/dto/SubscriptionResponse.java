package com.mcpgateway.admin.dto;

import java.util.UUID;

public record SubscriptionResponse(
        UUID id,
        UUID consumerId,
        UUID providerId,
        UUID versionId,
        String gatewayPath,
        String status
) {}
