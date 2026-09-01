package com.mcpgateway.admin.service;

import com.mcpgateway.admin.dto.ConsumerResponse;
import com.mcpgateway.admin.dto.CreateConsumerRequest;
import com.mcpgateway.admin.dto.CreateSubscriptionRequest;
import com.mcpgateway.admin.dto.SubscriptionResponse;
import com.mcpgateway.common.exception.ConflictException;
import com.mcpgateway.common.exception.ResourceNotFoundException;
import com.mcpgateway.domain.entity.Consumer;
import com.mcpgateway.domain.entity.ConsumerSubscription;
import com.mcpgateway.domain.entity.McpProvider;
import com.mcpgateway.domain.repository.ConsumerRepository;
import com.mcpgateway.domain.repository.ConsumerSubscriptionRepository;
import com.mcpgateway.domain.repository.McpProviderRepository;
import com.mcpgateway.security.SecurityUtils;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsumerService {

    private final ConsumerRepository consumerRepository;
    private final ConsumerSubscriptionRepository subscriptionRepository;
    private final McpProviderRepository providerRepository;

    public ConsumerService(
            ConsumerRepository consumerRepository,
            ConsumerSubscriptionRepository subscriptionRepository,
            McpProviderRepository providerRepository) {
        this.consumerRepository = consumerRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.providerRepository = providerRepository;
    }

    public List<ConsumerResponse> listConsumers() {
        UUID orgId = SecurityUtils.currentUser().orgId();
        return consumerRepository.findByOrgId(orgId).stream()
                .map(this::toConsumerResponse)
                .toList();
    }

    @Transactional
    public ConsumerResponse createConsumer(CreateConsumerRequest request) {
        UUID orgId = SecurityUtils.currentUser().orgId();
        if (consumerRepository.findByOrgIdAndSlug(orgId, request.slug()).isPresent()) {
            throw new ConflictException("Consumer slug already exists");
        }
        Consumer consumer = new Consumer();
        consumer.setOrgId(orgId);
        consumer.setSlug(request.slug());
        consumer.setDisplayName(request.displayName());
        consumer.setStatus("ACTIVE");
        consumerRepository.save(consumer);
        return toConsumerResponse(consumer);
    }

    @Transactional
    public SubscriptionResponse createSubscription(UUID consumerId, CreateSubscriptionRequest request) {
        UUID orgId = SecurityUtils.currentUser().orgId();
        Consumer consumer = consumerRepository.findById(consumerId)
                .orElseThrow(() -> new ResourceNotFoundException("Consumer not found"));
        if (!consumer.getOrgId().equals(orgId)) {
            throw new ResourceNotFoundException("Consumer not found");
        }
        McpProvider provider = providerRepository.findById(request.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
        if (!provider.getOrgId().equals(orgId)) {
            throw new ResourceNotFoundException("Provider not found");
        }

        ConsumerSubscription sub = new ConsumerSubscription();
        sub.setConsumerId(consumerId);
        sub.setProviderId(request.providerId());
        sub.setVersionId(request.versionId());
        sub.setGatewayPath("/mcp/" + orgId + "/" + provider.getSlug());
        sub.setStatus("ACTIVE");
        subscriptionRepository.save(sub);
        return toSubscriptionResponse(sub);
    }

    private ConsumerResponse toConsumerResponse(Consumer consumer) {
        return new ConsumerResponse(consumer.getId(), consumer.getSlug(), consumer.getDisplayName(), consumer.getStatus());
    }

    private SubscriptionResponse toSubscriptionResponse(ConsumerSubscription sub) {
        return new SubscriptionResponse(
                sub.getId(), sub.getConsumerId(), sub.getProviderId(),
                sub.getVersionId(), sub.getGatewayPath(), sub.getStatus());
    }
}
