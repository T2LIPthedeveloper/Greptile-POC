package com.mcpgateway.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateConsumerRequest(
        @NotBlank String slug,
        @NotBlank String displayName
) {}
