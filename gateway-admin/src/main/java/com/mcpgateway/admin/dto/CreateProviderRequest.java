package com.mcpgateway.admin.dto;

import com.mcpgateway.common.domain.ProviderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateProviderRequest(
        @NotBlank @Pattern(regexp = "[a-z0-9-]+") String slug,
        @NotBlank String displayName,
        String description,
        ProviderType providerType
) {}
