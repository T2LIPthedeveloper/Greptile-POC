package com.mcpgateway.admin.controller;

import com.mcpgateway.admin.service.AuditService;
import com.mcpgateway.domain.entity.AuditEvent;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit-events")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public List<AuditEvent> list() {
        return auditService.listForOrg();
    }
}
