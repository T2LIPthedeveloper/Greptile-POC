package com.mcpgateway.admin.dto;

import com.mcpgateway.common.domain.ContractSource;
import jakarta.validation.constraints.NotBlank;

public record CreateToolContractRequest(
        @NotBlank String toolName,
        String description,
        @NotBlank String inputSchema,
        String outputSchema,
        ContractSource source
) {}
