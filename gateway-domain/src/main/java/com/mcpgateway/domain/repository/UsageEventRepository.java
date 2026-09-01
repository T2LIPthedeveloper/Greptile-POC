package com.mcpgateway.domain.repository;

import com.mcpgateway.domain.entity.UsageEvent;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsageEventRepository extends JpaRepository<UsageEvent, UUID> {
    List<UsageEvent> findByOrgIdOrderByCreatedAtDesc(UUID orgId);
    long countByOrgId(UUID orgId);
}
