package com.mcpgateway.admin.controller;

import com.mcpgateway.admin.service.ComplianceService;
import com.mcpgateway.domain.entity.AuditEvent;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/compliance")
public class ComplianceController {

    private final ComplianceService complianceService;

    public ComplianceController(ComplianceService complianceService) {
        this.complianceService = complianceService;
    }

    @GetMapping("/audit-export")
    public List<AuditEvent> exportAudit() {
        return complianceService.exportRedactedAudit();
    }
}
