package com.mcpgateway.admin.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mcpgateway.admin.dto.ToolContractResponse;
import com.mcpgateway.common.domain.ContractSource;
import com.mcpgateway.common.exception.ResourceNotFoundException;
import com.mcpgateway.domain.entity.McpToolContract;
import com.mcpgateway.domain.repository.McpToolContractRepository;
import com.mcpgateway.domain.repository.McpVersionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OpenApiImportService {

    private final McpVersionRepository versionRepository;
    private final McpToolContractRepository toolContractRepository;
    private final ObjectMapper objectMapper;

    public OpenApiImportService(
            McpVersionRepository versionRepository,
            McpToolContractRepository toolContractRepository,
            ObjectMapper objectMapper) {
        this.versionRepository = versionRepository;
        this.toolContractRepository = toolContractRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public List<ToolContractResponse> importOpenApi(UUID versionId, String openApiJson) {
        if (!versionRepository.existsById(versionId)) {
            throw new ResourceNotFoundException("Version not found");
        }
        try {
            JsonNode root = objectMapper.readTree(openApiJson);
            JsonNode paths = root.path("paths");
            List<ToolContractResponse> imported = new ArrayList<>();
            paths.fields().forEachRemaining(pathEntry -> {
                String path = pathEntry.getKey();
                pathEntry.getValue().fields().forEachRemaining(methodEntry -> {
                    String method = methodEntry.getKey();
                    if (!method.equals("get") && !method.equals("post")) {
                        return;
                    }
                    String toolName = method + "_" + path.replace("/", "_").replaceAll("^_|_$", "");
                    ObjectNode schema = objectMapper.createObjectNode();
                    schema.put("type", "object");
                    schema.putObject("properties").putObject("path").put("type", "string").put("default", path);
                    McpToolContract contract = new McpToolContract();
                    contract.setVersionId(versionId);
                    contract.setToolName(toolName);
                    contract.setDescription("Imported from OpenAPI " + method.toUpperCase() + " " + path);
                    contract.setInputSchema(schema.toString());
                    contract.setOutputSchema("{\"type\":\"object\"}");
                    contract.setSource(ContractSource.IMPORTED);
                    toolContractRepository.save(contract);
                    imported.add(new ToolContractResponse(
                            contract.getId(),
                            contract.getToolName(),
                            contract.getDescription(),
                            contract.getInputSchema(),
                            contract.getOutputSchema(),
                            contract.getSource().name(),
                            contract.getCreatedAt()));
                });
            });
            return imported;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Invalid OpenAPI document: " + e.getMessage());
        }
    }
}
