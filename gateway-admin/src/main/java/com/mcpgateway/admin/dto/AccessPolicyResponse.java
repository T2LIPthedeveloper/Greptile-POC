package com.mcpgateway.admin.dto;

import java.time.Instant;
import java.util.UUID;

public record AccessPolicyResponse(
        UUID id,
        UUID subscriptionId,
        String policyType,
        String policyConfig,
        Instant createdAt) {}
