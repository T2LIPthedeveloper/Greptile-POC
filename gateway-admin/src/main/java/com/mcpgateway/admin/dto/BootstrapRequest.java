package com.mcpgateway.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record BootstrapRequest(
        @NotBlank String orgSlug,
        @NotBlank String orgName,
        @NotBlank @Email String email,
        @NotBlank String password
) {}
