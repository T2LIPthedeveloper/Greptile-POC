package com.mcpgateway.domain.repository;

import com.mcpgateway.domain.entity.ConsumerSubscription;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumerSubscriptionRepository extends JpaRepository<ConsumerSubscription, UUID> {
    Optional<ConsumerSubscription> findByProviderIdAndGatewayPath(UUID providerId, String gatewayPath);
    Optional<ConsumerSubscription> findByProviderId(UUID providerId);
    List<ConsumerSubscription> findAllByProviderId(UUID providerId);
}
