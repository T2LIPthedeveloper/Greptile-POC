package com.mcpgateway.admin.controller;

import com.mcpgateway.admin.dto.CreateEndpointRequest;
import com.mcpgateway.admin.dto.CreateToolContractRequest;
import com.mcpgateway.admin.dto.CreateVersionRequest;
import com.mcpgateway.admin.dto.EndpointResponse;
import com.mcpgateway.admin.dto.LinkCredentialRequest;
import com.mcpgateway.admin.dto.ToolContractResponse;
import com.mcpgateway.admin.dto.VersionResponse;
import com.mcpgateway.admin.service.VersionService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/providers/{providerId}/versions")
public class VersionController {

    private final VersionService versionService;

    public VersionController(VersionService versionService) {
        this.versionService = versionService;
    }

    @GetMapping
    public List<VersionResponse> list(@PathVariable UUID providerId) {
        return versionService.listVersions(providerId);
    }

    @GetMapping("/{versionId}")
    public VersionResponse get(@PathVariable UUID providerId, @PathVariable UUID versionId) {
        return versionService.getVersion(providerId, versionId);
    }

    @PostMapping
    public VersionResponse create(@PathVariable UUID providerId, @Valid @RequestBody CreateVersionRequest request) {
        return versionService.createVersion(providerId, request);
    }

    @PostMapping("/{versionId}/publish")
    public VersionResponse publish(@PathVariable UUID providerId, @PathVariable UUID versionId) {
        return versionService.publishVersion(providerId, versionId);
    }

    @PostMapping("/{versionId}/deprecate")
    public VersionResponse deprecate(@PathVariable UUID providerId, @PathVariable UUID versionId) {
        return versionService.deprecateVersion(providerId, versionId);
    }

    @PostMapping("/{versionId}/endpoints")
    public EndpointResponse addEndpoint(
            @PathVariable UUID providerId,
            @PathVariable UUID versionId,
            @Valid @RequestBody CreateEndpointRequest request) {
        return versionService.addEndpoint(providerId, versionId, request);
    }

    @GetMapping("/{versionId}/endpoints")
    public List<EndpointResponse> listEndpoints(@PathVariable UUID providerId, @PathVariable UUID versionId) {
        return versionService.listEndpoints(providerId, versionId);
    }

    @PostMapping("/{versionId}/tools")
    public ToolContractResponse addTool(
            @PathVariable UUID providerId,
            @PathVariable UUID versionId,
            @Valid @RequestBody CreateToolContractRequest request) {
        return versionService.addToolContract(providerId, versionId, request);
    }

    @GetMapping("/{versionId}/tools")
    public List<ToolContractResponse> listTools(@PathVariable UUID providerId, @PathVariable UUID versionId) {
        return versionService.listToolContracts(providerId, versionId);
    }

    @PostMapping("/{versionId}/credentials")
    public void linkCredential(
            @PathVariable UUID providerId,
            @PathVariable UUID versionId,
            @Valid @RequestBody LinkCredentialRequest request) {
        versionService.linkCredential(providerId, versionId, request);
    }
}
