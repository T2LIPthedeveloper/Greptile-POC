package com.mcpgateway.domain.repository;

import com.mcpgateway.domain.entity.AccessPolicy;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessPolicyRepository extends JpaRepository<AccessPolicy, UUID> {
    List<AccessPolicy> findBySubscriptionId(UUID subscriptionId);
}
