package com.mcpgateway.admin.dto;

import java.util.List;

public record ContractDiffResponse(
        List<String> addedTools,
        List<String> removedTools,
        List<String> changedTools) {}
