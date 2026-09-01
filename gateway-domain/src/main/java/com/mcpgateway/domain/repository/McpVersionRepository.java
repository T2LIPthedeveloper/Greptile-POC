package com.mcpgateway.domain.repository;

import com.mcpgateway.domain.entity.McpVersion;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface McpVersionRepository extends JpaRepository<McpVersion, UUID> {
    List<McpVersion> findByProviderId(UUID providerId);
    Optional<McpVersion> findByProviderIdAndVersionLabel(UUID providerId, String versionLabel);
    boolean existsByProviderIdAndVersionLabel(UUID providerId, String versionLabel);
}
