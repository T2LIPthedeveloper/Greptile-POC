package com.mcpgateway.admin.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcpgateway.admin.dto.DiscoveredToolResponse;
import com.mcpgateway.common.domain.ContractSource;
import com.mcpgateway.common.exception.ResourceNotFoundException;
import com.mcpgateway.domain.entity.McpEndpoint;
import com.mcpgateway.domain.entity.McpToolContract;
import com.mcpgateway.domain.entity.McpVersion;
import com.mcpgateway.domain.repository.McpEndpointRepository;
import com.mcpgateway.domain.repository.McpToolContractRepository;
import com.mcpgateway.domain.repository.McpVersionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ToolDiscoveryService {

    private final McpVersionRepository versionRepository;
    private final McpEndpointRepository endpointRepository;
    private final McpToolContractRepository toolContractRepository;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    public ToolDiscoveryService(
            McpVersionRepository versionRepository,
            McpEndpointRepository endpointRepository,
            McpToolContractRepository toolContractRepository,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder) {
        this.versionRepository = versionRepository;
        this.endpointRepository = endpointRepository;
        this.toolContractRepository = toolContractRepository;
        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder.build();
    }

    public List<DiscoveredToolResponse> discover(UUID versionId) {
        McpVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Version not found"));
        McpEndpoint endpoint = endpointRepository.findByVersionId(versionId).stream()
                .filter(McpEndpoint::isPrimary)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No primary endpoint for version"));

        String initializeBody = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"initialize\","
                + "\"params\":{\"protocolVersion\":\"" + version.getProtocolVersion() + "\","
                + "\"capabilities\":{},\"clientInfo\":{\"name\":\"mcp-gateway\",\"version\":\"0.1\"}}}";

        try {
            webClient.post()
                    .uri(endpoint.getBaseUrl())
                    .header("Content-Type", "application/json")
                    .bodyValue(initializeBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            String toolsBody = "{\"jsonrpc\":\"2.0\",\"id\":2,\"method\":\"tools/list\",\"params\":{}}";
            String toolsResponse = webClient.post()
                    .uri(endpoint.getBaseUrl())
                    .header("Content-Type", "application/json")
                    .bodyValue(toolsBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode root = objectMapper.readTree(toolsResponse);
            JsonNode tools = root.path("result").path("tools");
            List<DiscoveredToolResponse> discovered = new ArrayList<>();
            if (tools.isArray()) {
                for (JsonNode tool : tools) {
                    String schema = tool.has("inputSchema")
                            ? tool.get("inputSchema").toString()
                            : "{\"type\":\"object\"}";
                    discovered.add(new DiscoveredToolResponse(
                            tool.path("name").asText(),
                            tool.path("description").asText(null),
                            schema));
                }
            }
            return discovered;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Discovery failed: " + e.getMessage());
        }
    }

    @Transactional
    public List<DiscoveredToolResponse> discoverAndImport(UUID versionId) {
        List<DiscoveredToolResponse> discovered = discover(versionId);
        for (DiscoveredToolResponse tool : discovered) {
            if (toolContractRepository.findByVersionIdAndToolName(versionId, tool.name()).isPresent()) {
                continue;
            }
            McpToolContract contract = new McpToolContract();
            contract.setVersionId(versionId);
            contract.setToolName(tool.name());
            contract.setDescription(tool.description());
            contract.setInputSchema(tool.inputSchema());
            contract.setOutputSchema("{}");
            contract.setSource(ContractSource.DISCOVERED);
            toolContractRepository.save(contract);
        }
        return discovered;
    }
}
