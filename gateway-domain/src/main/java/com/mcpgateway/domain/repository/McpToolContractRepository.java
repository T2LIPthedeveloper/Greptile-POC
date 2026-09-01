package com.mcpgateway.domain.repository;

import com.mcpgateway.domain.entity.McpToolContract;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface McpToolContractRepository extends JpaRepository<McpToolContract, UUID> {
    List<McpToolContract> findByVersionId(UUID versionId);
}
