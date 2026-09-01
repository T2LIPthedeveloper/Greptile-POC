package com.mcpgateway.admin.dto;

import java.time.Instant;
import java.util.UUID;

public record SkillResponse(
        UUID id,
        String slug,
        String displayName,
        String description,
        String definition,
        String status,
        Instant createdAt) {}
