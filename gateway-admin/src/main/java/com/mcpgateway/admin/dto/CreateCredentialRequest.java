package com.mcpgateway.admin.dto;

import com.mcpgateway.common.domain.CredentialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCredentialRequest(
        @NotBlank String name,
        @NotNull CredentialType credentialType,
        @NotBlank String secretValue
) {}
