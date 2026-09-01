package com.mcpgateway.admin.service;

import com.mcpgateway.domain.entity.UsageEvent;
import com.mcpgateway.domain.repository.UsageEventRepository;
import com.mcpgateway.security.SecurityUtils;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsageMeteringService {

    private final UsageEventRepository usageEventRepository;

    public UsageMeteringService(UsageEventRepository usageEventRepository) {
        this.usageEventRepository = usageEventRepository;
    }

    @Transactional
    public void record(String eventType, String resourceType, UUID resourceId, long quantity) {
        UUID orgId = SecurityUtils.currentUser().orgId();
        UsageEvent event = new UsageEvent();
        event.setOrgId(orgId);
        event.setEventType(eventType);
        event.setResourceType(resourceType);
        event.setResourceId(resourceId);
        event.setQuantity(quantity);
        usageEventRepository.save(event);
    }

    public long countForOrg() {
        return usageEventRepository.countByOrgId(SecurityUtils.currentUser().orgId());
    }

    public List<UsageEvent> exportEvents() {
        return usageEventRepository.findByOrgIdOrderByCreatedAtDesc(SecurityUtils.currentUser().orgId());
    }

    public Map<String, Long> aggregateByType() {
        return usageEventRepository.findByOrgIdOrderByCreatedAtDesc(SecurityUtils.currentUser().orgId()).stream()
                .collect(Collectors.groupingBy(UsageEvent::getEventType, Collectors.summingLong(UsageEvent::getQuantity)));
    }
}
