package com.mcpgateway.admin.controller;

import com.mcpgateway.admin.dto.UsageSummaryResponse;
import com.mcpgateway.admin.service.UsageMeteringService;
import com.mcpgateway.domain.entity.UsageEvent;
import java.util.List;
import java.util.Map;
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

    @GetMapping("/aggregate")
    public Map<String, Long> aggregate() {
        return usageMeteringService.aggregateByType();
    }

    @GetMapping("/export")
    public List<UsageEvent> export() {
        return usageMeteringService.exportEvents();
    }
}
