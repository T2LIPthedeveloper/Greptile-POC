package com.mcpgateway.admin.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateSubscriptionRequest(
        @NotNull UUID providerId,
        UUID versionId
) {}
