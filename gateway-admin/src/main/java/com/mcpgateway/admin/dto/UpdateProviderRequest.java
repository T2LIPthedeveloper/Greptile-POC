package com.mcpgateway.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateProviderRequest(
        String displayName,
        String description
) {
    public boolean hasUpdates() {
        return displayName != null || description != null;
    }
}
