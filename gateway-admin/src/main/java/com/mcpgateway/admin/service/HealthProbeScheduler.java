package com.mcpgateway.admin.service;

import com.mcpgateway.domain.entity.McpEndpoint;
import com.mcpgateway.domain.repository.McpEndpointRepository;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class HealthProbeScheduler {

    private static final Logger log = LoggerFactory.getLogger(HealthProbeScheduler.class);

    private final McpEndpointRepository endpointRepository;
    private final WebClient webClient;

    public HealthProbeScheduler(McpEndpointRepository endpointRepository, WebClient.Builder webClientBuilder) {
        this.endpointRepository = endpointRepository;
        this.webClient = webClientBuilder.build();
    }

    @Scheduled(fixedDelayString = "${gateway.health-probe.interval-ms:300000}")
    public void probeEndpoints() {
        List<McpEndpoint> endpoints = endpointRepository.findAll();
        for (McpEndpoint endpoint : endpoints) {
            String healthUrl = endpoint.getBaseUrl().replaceAll("/$", "")
                    + endpoint.getHealthCheckPath();
            try {
                webClient.get().uri(healthUrl).retrieve().toBodilessEntity().block();
                endpoint.setLastHealthStatus("UP");
            } catch (Exception e) {
                endpoint.setLastHealthStatus("DOWN");
                log.debug("Health probe failed for {}: {}", healthUrl, e.getMessage());
            }
            endpoint.setLastHealthAt(Instant.now());
            endpointRepository.save(endpoint);
        }
    }
}
