package com.mcpgateway.admin.controller;

import com.mcpgateway.admin.dto.AccessPolicyResponse;
import com.mcpgateway.admin.dto.CreateAccessPolicyRequest;
import com.mcpgateway.admin.service.AccessPolicyService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/access-policies")
public class AccessPolicyController {

    private final AccessPolicyService accessPolicyService;

    public AccessPolicyController(AccessPolicyService accessPolicyService) {
        this.accessPolicyService = accessPolicyService;
    }

    @GetMapping("/subscription/{subscriptionId}")
    public List<AccessPolicyResponse> list(@PathVariable UUID subscriptionId) {
        return accessPolicyService.listForSubscription(subscriptionId);
    }

    @PostMapping
    public AccessPolicyResponse create(@Valid @RequestBody CreateAccessPolicyRequest request) {
        return accessPolicyService.create(request);
    }
}
