package com.mcpgateway.domain.repository;

import com.mcpgateway.domain.entity.McpProvider;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface McpProviderRepository extends JpaRepository<McpProvider, UUID> {
    List<McpProvider> findByOrgId(UUID orgId);
    Optional<McpProvider> findByOrgIdAndSlug(UUID orgId, String slug);
    boolean existsByOrgIdAndSlug(UUID orgId, String slug);
}
