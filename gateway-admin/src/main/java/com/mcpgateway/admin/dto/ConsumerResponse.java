package com.mcpgateway.admin.dto;

import java.util.UUID;

public record ConsumerResponse(
        UUID id,
        String slug,
        String displayName,
        String status
) {}
