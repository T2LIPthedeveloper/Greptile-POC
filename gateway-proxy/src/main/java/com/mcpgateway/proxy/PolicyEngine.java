package com.mcpgateway.proxy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcpgateway.domain.entity.AccessPolicy;
import com.mcpgateway.domain.entity.ConsumerSubscription;
import com.mcpgateway.domain.repository.AccessPolicyRepository;
import com.mcpgateway.domain.repository.ConsumerSubscriptionRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PolicyEngine {

    private final ConsumerSubscriptionRepository subscriptionRepository;
    private final AccessPolicyRepository accessPolicyRepository;
    private final ObjectMapper objectMapper;

    public PolicyEngine(
            ConsumerSubscriptionRepository subscriptionRepository,
            AccessPolicyRepository accessPolicyRepository,
            ObjectMapper objectMapper) {
        this.subscriptionRepository = subscriptionRepository;
        this.accessPolicyRepository = accessPolicyRepository;
        this.objectMapper = objectMapper;
    }

    public boolean isToolDenied(UUID providerId, String requestBody) {
        String toolName = extractToolName(requestBody);
        if (toolName == null) {
            return false;
        }
        Set<String> denied = loadDeniedTools(providerId);
        return denied.contains(toolName);
    }

    private Set<String> loadDeniedTools(UUID providerId) {
        Set<String> denied = new HashSet<>();
        List<ConsumerSubscription> subs = subscriptionRepository.findAllByProviderId(providerId);
        for (ConsumerSubscription sub : subs) {
            List<AccessPolicy> policies = accessPolicyRepository.findBySubscriptionId(sub.getId());
            for (AccessPolicy policy : policies) {
                if ("TOOL_DENY".equals(policy.getPolicyType())) {
                    try {
                        JsonNode config = objectMapper.readTree(policy.getPolicyConfig());
                        JsonNode tools = config.path("tools");
                        if (tools.isArray()) {
                            tools.forEach(t -> denied.add(t.asText()));
                        }
                    } catch (Exception ignored) {
                        // skip invalid policy config
                    }
                }
            }
        }
        return denied;
    }

    private String extractToolName(String body) {
        try {
            JsonNode root = objectMapper.readTree(body);
            if ("tools/call".equals(root.path("method").asText())) {
                return root.path("params").path("name").asText(null);
            }
        } catch (Exception ignored) {
            // not JSON-RPC
        }
        return null;
    }
}
