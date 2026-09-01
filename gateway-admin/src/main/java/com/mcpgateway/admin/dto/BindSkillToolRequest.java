package com.mcpgateway.admin.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record BindSkillToolRequest(
        @NotBlank String toolName,
        UUID versionId) {}
