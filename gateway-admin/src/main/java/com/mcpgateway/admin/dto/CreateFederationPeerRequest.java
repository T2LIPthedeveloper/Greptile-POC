package com.mcpgateway.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateFederationPeerRequest(
        @NotBlank @Pattern(regexp = "[a-z0-9-]+") String slug,
        @NotBlank String displayName,
        @NotBlank String peerUrl
) {}
