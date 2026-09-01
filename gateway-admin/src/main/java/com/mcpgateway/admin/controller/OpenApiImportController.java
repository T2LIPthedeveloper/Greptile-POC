package com.mcpgateway.admin.controller;

import com.mcpgateway.admin.dto.ToolContractResponse;
import com.mcpgateway.admin.service.AiSuggestionService;
import com.mcpgateway.admin.service.OpenApiImportService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/providers/{providerId}/versions/{versionId}")
public class OpenApiImportController {

    private final OpenApiImportService openApiImportService;
    private final AiSuggestionService aiSuggestionService;

    public OpenApiImportController(OpenApiImportService openApiImportService, AiSuggestionService aiSuggestionService) {
        this.openApiImportService = openApiImportService;
        this.aiSuggestionService = aiSuggestionService;
    }

    @PostMapping("/import-openapi")
    public List<ToolContractResponse> importOpenApi(
            @PathVariable UUID providerId,
            @PathVariable UUID versionId,
            @RequestBody Map<String, String> body) {
        return openApiImportService.importOpenApi(versionId, body.get("openApiJson"));
    }

    @PostMapping("/ai-suggestions")
    public List<Map<String, String>> suggestions(
            @PathVariable UUID providerId,
            @PathVariable UUID versionId,
            @RequestBody Map<String, String> body) {
        String snippet = body.getOrDefault("context", "");
        return aiSuggestionService.suggestContractImprovements(snippet);
    }
}
