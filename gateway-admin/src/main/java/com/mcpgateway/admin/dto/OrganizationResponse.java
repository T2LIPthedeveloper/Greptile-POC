package com.mcpgateway.admin.dto;

import java.util.UUID;

public record OrganizationResponse(UUID id, String slug, String name, String status) {}
