package com.mcpgateway.admin.controller;

import com.mcpgateway.admin.dto.ApprovalResponse;
import com.mcpgateway.admin.service.ApprovalService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping("/pending")
    public List<ApprovalResponse> listPending() {
        return approvalService.listPending();
    }

    @PostMapping("/{id}/approve")
    public ApprovalResponse approve(@PathVariable UUID id, @RequestBody(required = false) Map<String, String> body) {
        String notes = body != null ? body.get("notes") : null;
        return approvalService.approve(id, notes);
    }
}
