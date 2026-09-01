package com.mcpgateway.admin.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcpgateway.common.exception.ConflictException;
import com.mcpgateway.domain.entity.McpToolContract;
import com.mcpgateway.domain.repository.McpToolContractRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ContractValidationService {

    private final McpToolContractRepository toolContractRepository;
    private final ObjectMapper objectMapper;

    public ContractValidationService(McpToolContractRepository toolContractRepository, ObjectMapper objectMapper) {
        this.toolContractRepository = toolContractRepository;
        this.objectMapper = objectMapper;
    }

    public ValidationResult validateVersion(UUID versionId) {
        List<McpToolContract> tools = toolContractRepository.findByVersionId(versionId);
        List<String> errors = new ArrayList<>();
        if (tools.isEmpty()) {
            errors.add("At least one tool contract is required");
        }
        for (McpToolContract tool : tools) {
            try {
                JsonNode schema = objectMapper.readTree(tool.getInputSchema());
                if (!schema.has("type")) {
                    errors.add("Tool " + tool.getToolName() + ": input_schema must include 'type'");
                }
            } catch (Exception e) {
                errors.add("Tool " + tool.getToolName() + ": invalid JSON schema");
            }
        }
        return new ValidationResult(errors.isEmpty(), errors);
    }

    public void assertValidForPublish(UUID versionId) {
        ValidationResult result = validateVersion(versionId);
        if (!result.valid()) {
            throw new ConflictException("Contract validation failed: " + String.join("; ", result.errors()));
        }
    }

    public record ValidationResult(boolean valid, List<String> errors) {}
}
