package com.mcpgateway.admin.service;

import com.mcpgateway.admin.dto.AccessPolicyResponse;
import com.mcpgateway.admin.dto.CreateAccessPolicyRequest;
import com.mcpgateway.domain.entity.AccessPolicy;
import com.mcpgateway.domain.repository.AccessPolicyRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccessPolicyService {

    private final AccessPolicyRepository accessPolicyRepository;

    public AccessPolicyService(AccessPolicyRepository accessPolicyRepository) {
        this.accessPolicyRepository = accessPolicyRepository;
    }

    public List<AccessPolicyResponse> listForSubscription(UUID subscriptionId) {
        return accessPolicyRepository.findBySubscriptionId(subscriptionId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AccessPolicyResponse create(CreateAccessPolicyRequest request) {
        AccessPolicy policy = new AccessPolicy();
        policy.setSubscriptionId(request.subscriptionId());
        policy.setPolicyType(request.policyType());
        policy.setPolicyConfig(request.policyConfig());
        accessPolicyRepository.save(policy);
        return toResponse(policy);
    }

    private AccessPolicyResponse toResponse(AccessPolicy policy) {
        return new AccessPolicyResponse(
                policy.getId(),
                policy.getSubscriptionId(),
                policy.getPolicyType(),
                policy.getPolicyConfig(),
                policy.getCreatedAt());
    }
}
