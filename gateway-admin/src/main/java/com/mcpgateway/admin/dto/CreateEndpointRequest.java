package com.mcpgateway.admin.dto;

import com.mcpgateway.common.domain.TransportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateEndpointRequest(
        @NotNull TransportType transport,
        @NotBlank String baseUrl,
        String healthCheckPath,
        Integer timeoutMs,
        Boolean primary
) {}
