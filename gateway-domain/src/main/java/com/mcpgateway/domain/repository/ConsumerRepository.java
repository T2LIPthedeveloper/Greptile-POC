package com.mcpgateway.domain.repository;

import com.mcpgateway.domain.entity.Consumer;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumerRepository extends JpaRepository<Consumer, UUID> {
    Optional<Consumer> findByOrgIdAndSlug(UUID orgId, String slug);
}
