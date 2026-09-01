package com.mcpgateway.domain.repository;

import com.mcpgateway.domain.entity.McpEndpoint;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface McpEndpointRepository extends JpaRepository<McpEndpoint, UUID> {
    List<McpEndpoint> findByVersionId(UUID versionId);
}
