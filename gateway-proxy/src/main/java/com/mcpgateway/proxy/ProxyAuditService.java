package com.mcpgateway.proxy;

import com.mcpgateway.domain.entity.AuditEvent;
import com.mcpgateway.domain.repository.AuditEventRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProxyAuditService {

    private final AuditEventRepository auditEventRepository;

    public ProxyAuditService(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    @Transactional
    public void recordToolCall(UUID orgId, UUID providerId, String toolName, String correlationId) {
        AuditEvent event = new AuditEvent();
        event.setOrgId(orgId);
        event.setActorType("CONSUMER");
        event.setActorId("gateway-proxy");
        event.setAction("TOOL_CALL");
        event.setResourceType("mcp_provider");
        event.setResourceId(providerId);
        event.setMetadata(toolName != null ? "{\"tool\":\"" + toolName + "\"}" : null);
        event.setCorrelationId(correlationId);
        auditEventRepository.save(event);
    }

    @Transactional
    public void recordDenied(UUID orgId, UUID providerId, String toolName, String correlationId) {
        AuditEvent event = new AuditEvent();
        event.setOrgId(orgId);
        event.setActorType("CONSUMER");
        event.setActorId("gateway-proxy");
        event.setAction("TOOL_DENIED");
        event.setResourceType("mcp_provider");
        event.setResourceId(providerId);
        event.setMetadata(toolName != null ? "{\"tool\":\"" + toolName + "\",\"reason\":\"policy\"}" : null);
        event.setCorrelationId(correlationId);
        auditEventRepository.save(event);
    }
}
