package com.mcpgateway.admin.dto;

import java.time.Instant;
import java.util.UUID;

public record ApprovalResponse(
        UUID id,
        UUID versionId,
        String status,
        UUID requestedBy,
        UUID reviewedBy,
        Instant reviewedAt,
        String notes,
        Instant createdAt) {}
