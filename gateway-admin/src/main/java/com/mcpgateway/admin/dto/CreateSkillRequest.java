package com.mcpgateway.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSkillRequest(
        @NotBlank String slug,
        @NotBlank String displayName,
        String description,
        @NotBlank String definition) {}
