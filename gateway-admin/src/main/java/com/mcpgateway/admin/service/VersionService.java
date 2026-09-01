package com.mcpgateway.admin.service;

import com.mcpgateway.admin.dto.CreateEndpointRequest;
import com.mcpgateway.admin.dto.CreateToolContractRequest;
import com.mcpgateway.admin.dto.CreateVersionRequest;
import com.mcpgateway.admin.dto.EndpointResponse;
import com.mcpgateway.admin.dto.LinkCredentialRequest;
import com.mcpgateway.admin.dto.ToolContractResponse;
import com.mcpgateway.admin.dto.VersionResponse;
import com.mcpgateway.common.domain.ContractSource;
import com.mcpgateway.common.domain.VersionStatus;
import com.mcpgateway.common.exception.ConflictException;
import com.mcpgateway.common.exception.ResourceNotFoundException;
import com.mcpgateway.domain.entity.McpEndpoint;
import com.mcpgateway.domain.entity.McpProvider;
import com.mcpgateway.domain.entity.McpToolContract;
import com.mcpgateway.domain.entity.McpVersion;
import com.mcpgateway.domain.entity.VersionCredentialEntity;
import com.mcpgateway.domain.repository.McpEndpointRepository;
import com.mcpgateway.domain.repository.McpProviderRepository;
import com.mcpgateway.domain.repository.McpToolContractRepository;
import com.mcpgateway.domain.repository.McpVersionRepository;
import com.mcpgateway.domain.repository.VersionCredentialRepository;
import com.mcpgateway.security.SecurityUtils;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VersionService {

    private final McpProviderRepository providerRepository;
    private final McpVersionRepository versionRepository;
    private final McpEndpointRepository endpointRepository;
    private final McpToolContractRepository toolContractRepository;
    private final VersionCredentialRepository versionCredentialRepository;

    public VersionService(
            McpProviderRepository providerRepository,
            McpVersionRepository versionRepository,
            McpEndpointRepository endpointRepository,
            McpToolContractRepository toolContractRepository,
            VersionCredentialRepository versionCredentialRepository) {
        this.providerRepository = providerRepository;
        this.versionRepository = versionRepository;
        this.endpointRepository = endpointRepository;
        this.toolContractRepository = toolContractRepository;
        this.versionCredentialRepository = versionCredentialRepository;
    }

    public List<VersionResponse> listVersions(UUID providerId) {
        findProviderForOrg(providerId);
        return versionRepository.findByProviderId(providerId).stream()
                .map(this::toResponse)
                .toList();
    }

    public VersionResponse getVersion(UUID providerId, UUID versionId) {
        findProviderForOrg(providerId);
        return toResponse(findVersion(providerId, versionId));
    }

    @Transactional
    public VersionResponse createVersion(UUID providerId, CreateVersionRequest request) {
        findProviderForOrg(providerId);
        if (versionRepository.existsByProviderIdAndVersionLabel(providerId, request.versionLabel())) {
            throw new ConflictException("Version label already exists");
        }

        McpVersion version = new McpVersion();
        version.setProviderId(providerId);
        version.setVersionLabel(request.versionLabel());
        version.setProtocolVersion(request.protocolVersion() != null ? request.protocolVersion() : "2025-11-25");
        version.setChangelog(request.changelog());
        version.setStatus(VersionStatus.DRAFT);
        versionRepository.save(version);
        return toResponse(version);
    }

    @Transactional
    public EndpointResponse addEndpoint(UUID providerId, UUID versionId, CreateEndpointRequest request) {
        McpVersion version = findDraftVersion(providerId, versionId);

        McpEndpoint endpoint = new McpEndpoint();
        endpoint.setVersionId(version.getId());
        endpoint.setTransport(request.transport());
        endpoint.setBaseUrl(request.baseUrl());
        endpoint.setHealthCheckPath(request.healthCheckPath() != null ? request.healthCheckPath() : "/health");
        endpoint.setTimeoutMs(request.timeoutMs() != null ? request.timeoutMs() : 30000);
        endpoint.setPrimary(request.primary() != null ? request.primary() : true);
        endpointRepository.save(endpoint);

        return new EndpointResponse(
                endpoint.getId(),
                endpoint.getTransport().name(),
                endpoint.getBaseUrl(),
                endpoint.getHealthCheckPath(),
                endpoint.getTimeoutMs(),
                endpoint.isPrimary());
    }

    @Transactional
    public ToolContractResponse addToolContract(UUID providerId, UUID versionId, CreateToolContractRequest request) {
        McpVersion version = findDraftVersion(providerId, versionId);

        McpToolContract contract = new McpToolContract();
        contract.setVersionId(version.getId());
        contract.setToolName(request.toolName());
        contract.setDescription(request.description());
        contract.setInputSchema(request.inputSchema());
        contract.setOutputSchema(request.outputSchema());
        contract.setSource(request.source() != null ? request.source() : ContractSource.MANUAL);
        toolContractRepository.save(contract);

        return toToolResponse(contract);
    }

    @Transactional
    public void linkCredential(UUID providerId, UUID versionId, LinkCredentialRequest request) {
        McpVersion version = findDraftVersion(providerId, versionId);

        VersionCredentialEntity link = new VersionCredentialEntity();
        link.setVersionId(version.getId());
        link.setCredentialId(request.credentialId());
        link.setUsage(request.usage());
        versionCredentialRepository.save(link);
    }

    public List<EndpointResponse> listEndpoints(UUID providerId, UUID versionId) {
        findVersion(providerId, versionId);
        return endpointRepository.findByVersionId(versionId).stream()
                .map(e -> new EndpointResponse(
                        e.getId(), e.getTransport().name(), e.getBaseUrl(),
                        e.getHealthCheckPath(), e.getTimeoutMs(), e.isPrimary()))
                .toList();
    }

    public List<ToolContractResponse> listToolContracts(UUID providerId, UUID versionId) {
        findVersion(providerId, versionId);
        return toolContractRepository.findByVersionId(versionId).stream()
                .map(this::toToolResponse)
                .toList();
    }

    private McpProvider findProviderForOrg(UUID providerId) {
        UUID orgId = SecurityUtils.currentUser().orgId();
        McpProvider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
        if (!provider.getOrgId().equals(orgId)) {
            throw new ResourceNotFoundException("Provider not found");
        }
        return provider;
    }

    private McpVersion findVersion(UUID providerId, UUID versionId) {
        McpVersion version = versionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Version not found"));
        if (!version.getProviderId().equals(providerId)) {
            throw new ResourceNotFoundException("Version not found");
        }
        return version;
    }

    private McpVersion findDraftVersion(UUID providerId, UUID versionId) {
        McpVersion version = findVersion(providerId, versionId);
        if (version.getStatus() != VersionStatus.DRAFT) {
            throw new ConflictException("Only DRAFT versions can be modified");
        }
        return version;
    }

    private VersionResponse toResponse(McpVersion version) {
        return new VersionResponse(
                version.getId(),
                version.getProviderId(),
                version.getVersionLabel(),
                version.getProtocolVersion(),
                version.getStatus().name(),
                version.getChangelog(),
                version.getPublishedAt(),
                version.getCreatedAt());
    }

    private ToolContractResponse toToolResponse(McpToolContract contract) {
        return new ToolContractResponse(
                contract.getId(),
                contract.getToolName(),
                contract.getDescription(),
                contract.getInputSchema(),
                contract.getOutputSchema(),
                contract.getSource().name(),
                contract.getCreatedAt());
    }
}
