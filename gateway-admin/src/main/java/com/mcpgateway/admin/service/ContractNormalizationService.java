package com.mcpgateway.admin.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mcpgateway.domain.entity.McpToolContract;
import com.mcpgateway.domain.repository.McpToolContractRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContractNormalizationService {

    private final McpToolContractRepository toolContractRepository;
    private final ObjectMapper objectMapper;

    public ContractNormalizationService(McpToolContractRepository toolContractRepository, ObjectMapper objectMapper) {
        this.toolContractRepository = toolContractRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public int normalizeVersion(UUID versionId) {
        List<McpToolContract> tools = toolContractRepository.findByVersionId(versionId);
        int normalized = 0;
        for (McpToolContract tool : tools) {
            try {
                JsonNode schema = objectMapper.readTree(tool.getInputSchema());
                if (schema.isObject()) {
                    ObjectNode obj = (ObjectNode) schema;
                    if (!obj.has("type")) {
                        obj.put("type", "object");
                    }
                    if (!obj.has("$schema")) {
                        obj.put("$schema", "https://json-schema.org/draft/2020-12/schema");
                    }
                    tool.setInputSchema(obj.toString());
                    toolContractRepository.save(tool);
                    normalized++;
                }
            } catch (Exception ignored) {
                // skip invalid
            }
        }
        return normalized;
    }
}
