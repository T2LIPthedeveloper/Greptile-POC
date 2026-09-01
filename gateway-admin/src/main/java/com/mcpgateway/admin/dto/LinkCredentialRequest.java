package com.mcpgateway.admin.dto;

import com.mcpgateway.common.domain.CredentialUsage;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record LinkCredentialRequest(
        @NotNull UUID credentialId,
        @NotNull CredentialUsage usage
) {}
