package com.mcpgateway.proxy;

import com.mcpgateway.common.crypto.CredentialEncryptor;
import com.mcpgateway.common.exception.ResourceNotFoundException;
import com.mcpgateway.domain.entity.CredentialVault;
import com.mcpgateway.domain.entity.McpEndpoint;
import com.mcpgateway.domain.entity.McpProvider;
import com.mcpgateway.domain.entity.McpVersion;
import com.mcpgateway.domain.entity.Organization;
import com.mcpgateway.domain.entity.VersionCredentialEntity;
import com.mcpgateway.domain.repository.CredentialVaultRepository;
import com.mcpgateway.domain.repository.McpEndpointRepository;
import com.mcpgateway.domain.repository.McpProviderRepository;
import com.mcpgateway.domain.repository.McpVersionRepository;
import com.mcpgateway.domain.repository.OrganizationRepository;
import com.mcpgateway.domain.repository.VersionCredentialRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class RouteResolver {

    private final OrganizationRepository organizationRepository;
    private final McpProviderRepository providerRepository;
    private final McpVersionRepository versionRepository;
    private final McpEndpointRepository endpointRepository;
    private final VersionCredentialRepository versionCredentialRepository;
    private final CredentialVaultRepository credentialVaultRepository;
    private final CredentialEncryptor encryptor;

    public RouteResolver(
            OrganizationRepository organizationRepository,
            McpProviderRepository providerRepository,
            McpVersionRepository versionRepository,
            McpEndpointRepository endpointRepository,
            VersionCredentialRepository versionCredentialRepository,
            CredentialVaultRepository credentialVaultRepository,
            CredentialEncryptor encryptor) {
        this.organizationRepository = organizationRepository;
        this.providerRepository = providerRepository;
        this.versionRepository = versionRepository;
        this.endpointRepository = endpointRepository;
        this.versionCredentialRepository = versionCredentialRepository;
        this.credentialVaultRepository = credentialVaultRepository;
        this.encryptor = encryptor;
    }

    public ResolvedRoute resolve(String orgSlug, String providerSlug) {
        Organization org = organizationRepository.findBySlug(orgSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        McpProvider provider = providerRepository.findByOrgIdAndSlug(org.getId(), providerSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        List<McpVersion> versions = versionRepository.findByProviderId(provider.getId());
        McpVersion version = versions.stream()
                .filter(v -> v.getStatus().name().equals("PUBLISHED"))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No published version"));

        McpEndpoint endpoint = endpointRepository.findByVersionId(version.getId()).stream()
                .filter(McpEndpoint::isPrimary)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No endpoint configured"));

        String upstreamAuth = resolveUpstreamAuth(version.getId());

        return new ResolvedRoute(
                org.getId(),
                provider.getId(),
                endpoint.getBaseUrl(),
                upstreamAuth,
                version.getProtocolVersion());
    }

    private String resolveUpstreamAuth(UUID versionId) {
        List<VersionCredentialEntity> links = versionCredentialRepository.findByVersionId(versionId);
        if (links.isEmpty()) {
            return null;
        }
        UUID credentialId = links.get(0).getCredentialId();
        CredentialVault vault = credentialVaultRepository.findById(credentialId)
                .orElse(null);
        if (vault == null) {
            return null;
        }
        return encryptor.decrypt(vault.getEncryptedPayload());
    }

    public record ResolvedRoute(
            UUID orgId,
            UUID providerId,
            String upstreamBaseUrl,
            String upstreamApiKey,
            String protocolVersion) {}
}
