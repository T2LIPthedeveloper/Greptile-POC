package com.mcpgateway.admin.service;

import com.mcpgateway.domain.entity.AuditEvent;
import com.mcpgateway.domain.repository.AuditEventRepository;
import com.mcpgateway.security.AuthenticatedUser;
import com.mcpgateway.security.SecurityUtils;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    public AuditService(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    public List<AuditEvent> listForOrg() {
        UUID orgId = SecurityUtils.currentUser().orgId();
        return auditEventRepository.findByOrgIdOrderByCreatedAtDesc(orgId);
    }

    @Transactional
    public void record(String action, String resourceType, UUID resourceId, String metadata) {
        AuthenticatedUser user = SecurityUtils.currentUser();
        AuditEvent event = new AuditEvent();
        event.setOrgId(user.orgId());
        event.setActorType("USER");
        event.setActorId(user.userId().toString());
        event.setAction(action);
        event.setResourceType(resourceType);
        event.setResourceId(resourceId);
        event.setMetadata(metadata);
        auditEventRepository.save(event);
    }
}
