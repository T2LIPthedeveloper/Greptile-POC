package com.mcpgateway.admin.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AiSuggestionService {

    public List<Map<String, String>> suggestPolicies(String context) {
        return List.of(
                Map.of("type", "TOOL_DENY", "suggestion", "Deny destructive tools: delete_*, remove_*"),
                Map.of("type", "TOOL_ALLOW", "suggestion", "Allow read-only tools for consumer tier"));
    }

    public List<Map<String, String>> suggestContractImprovements(String openApiSnippet) {
        return List.of(
                Map.of("field", "input_schema", "suggestion", "Add required fields from OpenAPI parameters"),
                Map.of("field", "description", "suggestion", "Enrich tool descriptions from OpenAPI summaries"));
    }
}
