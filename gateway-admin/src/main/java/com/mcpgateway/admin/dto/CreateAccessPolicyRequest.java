package com.mcpgateway.admin.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record CreateAccessPolicyRequest(
        UUID subscriptionId,
        @NotBlank String policyType,
        @NotBlank String policyConfig) {}
