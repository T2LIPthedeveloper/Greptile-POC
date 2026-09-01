package com.mcpgateway.security;

import java.util.List;
import java.util.UUID;

public record AuthenticatedUser(
        UUID userId,
        UUID orgId,
        String email,
        List<String> roles
) {}
