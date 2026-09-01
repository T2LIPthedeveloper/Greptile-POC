package com.mcpgateway.domain.repository;

import com.mcpgateway.domain.entity.FederationPeer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FederationPeerRepository extends JpaRepository<FederationPeer, UUID> {
    List<FederationPeer> findByOrgId(UUID orgId);
    Optional<FederationPeer> findByOrgIdAndSlug(UUID orgId, String slug);
}
