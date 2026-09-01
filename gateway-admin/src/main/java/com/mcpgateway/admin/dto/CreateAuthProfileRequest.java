package com.mcpgateway.admin.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record CreateAuthProfileRequest(
        @NotBlank String name,
        @NotBlank String authMethod,
        String config,
        UUID credentialId) {}
