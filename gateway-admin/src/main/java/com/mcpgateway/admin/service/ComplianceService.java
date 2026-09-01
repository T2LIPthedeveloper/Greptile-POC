package com.mcpgateway.admin.service;

import com.mcpgateway.domain.entity.AuditEvent;
import com.mcpgateway.domain.repository.AuditEventRepository;
import com.mcpgateway.security.SecurityUtils;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ComplianceService {

    private final AuditEventRepository auditEventRepository;

    public ComplianceService(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    public List<AuditEvent> exportRedactedAudit() {
        UUID orgId = SecurityUtils.currentUser().orgId();
        return auditEventRepository.findByOrgIdOrderByCreatedAtDesc(orgId).stream()
                .map(this::redact)
                .toList();
    }

    private AuditEvent redact(AuditEvent event) {
        AuditEvent copy = new AuditEvent();
        copy.setId(event.getId());
        copy.setOrgId(event.getOrgId());
        copy.setActorType(event.getActorType());
        copy.setActorId(redactValue(event.getActorId()));
        copy.setAction(event.getAction());
        copy.setResourceType(event.getResourceType());
        copy.setResourceId(event.getResourceId());
        copy.setMetadata(redactMetadata(event.getMetadata()));
        copy.setIpAddress(redactValue(event.getIpAddress()));
        copy.setCorrelationId(event.getCorrelationId());
        return copy;
    }

    private String redactValue(String value) {
        if (value == null || value.length() < 4) {
            return "***";
        }
        return value.substring(0, 2) + "***" + value.substring(value.length() - 2);
    }

    private String redactMetadata(String metadata) {
        if (metadata == null) {
            return null;
        }
        return metadata.replaceAll("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", "[REDACTED_EMAIL]");
    }
}
