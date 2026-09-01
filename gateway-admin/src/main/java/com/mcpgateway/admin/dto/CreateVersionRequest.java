package com.mcpgateway.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateVersionRequest(
        @NotBlank String versionLabel,
        String protocolVersion,
        String changelog
) {}
