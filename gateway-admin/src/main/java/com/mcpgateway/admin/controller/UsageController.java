package com.mcpgateway.admin.controller;

import com.mcpgateway.admin.dto.UsageSummaryResponse;
import com.mcpgateway.admin.service.UsageMeteringService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usage")
public class UsageController {

    private final UsageMeteringService usageMeteringService;

    public UsageController(UsageMeteringService usageMeteringService) {
        this.usageMeteringService = usageMeteringService;
    }

    @GetMapping("/summary")
    public UsageSummaryResponse summary() {
        return new UsageSummaryResponse(usageMeteringService.countForOrg());
    }
}
