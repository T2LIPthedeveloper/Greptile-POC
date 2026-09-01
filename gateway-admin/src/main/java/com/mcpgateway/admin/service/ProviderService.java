package com.mcpgateway.admin.service;

import com.mcpgateway.admin.dto.CreateProviderRequest;
import com.mcpgateway.admin.dto.ProviderResponse;
import com.mcpgateway.admin.dto.UpdateProviderRequest;
import com.mcpgateway.common.domain.ProviderStatus;
import com.mcpgateway.common.domain.ProviderType;
import com.mcpgateway.common.exception.ConflictException;
import com.mcpgateway.common.exception.ResourceNotFoundException;
import com.mcpgateway.domain.entity.McpProvider;
import com.mcpgateway.domain.repository.McpProviderRepository;
import com.mcpgateway.security.AuthenticatedUser;
import com.mcpgateway.security.SecurityUtils;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProviderService {

    private final McpProviderRepository providerRepository;

    public ProviderService(McpProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    public List<ProviderResponse> listProviders() {
        UUID orgId = SecurityUtils.currentUser().orgId();
        return providerRepository.findByOrgId(orgId).stream()
                .map(this::toResponse)
                .toList();
    }

    public ProviderResponse getProvider(UUID id) {
        McpProvider provider = findProviderForOrg(id);
        return toResponse(provider);
    }

    @Transactional
    public ProviderResponse createProvider(CreateProviderRequest request) {
        AuthenticatedUser user = SecurityUtils.currentUser();
        if (providerRepository.existsByOrgIdAndSlug(user.orgId(), request.slug())) {
            throw new ConflictException("Provider slug already exists in organization");
        }

        McpProvider provider = new McpProvider();
        provider.setOrgId(user.orgId());
        provider.setSlug(request.slug());
        provider.setDisplayName(request.displayName());
        provider.setDescription(request.description());
        provider.setOwnerUserId(user.userId());
        provider.setStatus(ProviderStatus.DRAFT);
        provider.setProviderType(request.providerType() != null ? request.providerType() : ProviderType.REMOTE_HTTP);
        providerRepository.save(provider);
        return toResponse(provider);
    }

    @Transactional
    public ProviderResponse updateProvider(UUID id, UpdateProviderRequest request) {
        McpProvider provider = findProviderForOrg(id);
        if (request.displayName() != null) {
            provider.setDisplayName(request.displayName());
        }
        if (request.description() != null) {
            provider.setDescription(request.description());
        }
        providerRepository.save(provider);
        return toResponse(provider);
    }

  private McpProvider findProviderForOrg(UUID id) {
        AuthenticatedUser user = SecurityUtils.currentUser();
        McpProvider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
        if (!provider.getOrgId().equals(user.orgId())) {
            throw new ResourceNotFoundException("Provider not found");
        }
        return provider;
    }

    private ProviderResponse toResponse(McpProvider provider) {
        return new ProviderResponse(
                provider.getId(),
                provider.getSlug(),
                provider.getDisplayName(),
                provider.getDescription(),
                provider.getStatus().name(),
                provider.getProviderType().name(),
                provider.getCreatedAt(),
                provider.getUpdatedAt());
    }
}
