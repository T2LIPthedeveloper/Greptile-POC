package com.mcpgateway.admin.service;

import com.mcpgateway.admin.dto.CreateCredentialRequest;
import com.mcpgateway.admin.dto.CredentialResponse;
import com.mcpgateway.common.crypto.CredentialEncryptor;
import com.mcpgateway.common.exception.ResourceNotFoundException;
import com.mcpgateway.domain.entity.CredentialVault;
import com.mcpgateway.domain.repository.CredentialVaultRepository;
import com.mcpgateway.security.SecurityUtils;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CredentialService {

    private final CredentialVaultRepository credentialRepository;
    private final CredentialEncryptor encryptor;

    public CredentialService(CredentialVaultRepository credentialRepository, CredentialEncryptor encryptor) {
        this.credentialRepository = credentialRepository;
        this.encryptor = encryptor;
    }

    public List<CredentialResponse> listCredentials() {
        UUID orgId = SecurityUtils.currentUser().orgId();
        return credentialRepository.findByOrgId(orgId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CredentialResponse createCredential(CreateCredentialRequest request) {
        UUID orgId = SecurityUtils.currentUser().orgId();

        CredentialVault vault = new CredentialVault();
        vault.setOrgId(orgId);
        vault.setName(request.name());
        vault.setCredentialType(request.credentialType());
        vault.setEncryptedPayload(encryptor.encrypt(request.secretValue()));
        vault.setKeyVersion(1);
        credentialRepository.save(vault);

        return toResponse(vault);
    }

    public CredentialResponse getCredential(UUID id) {
        CredentialVault vault = findForOrg(id);
        return toResponse(vault);
    }

    private CredentialVault findForOrg(UUID id) {
        UUID orgId = SecurityUtils.currentUser().orgId();
        CredentialVault vault = credentialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credential not found"));
        if (!vault.getOrgId().equals(orgId)) {
            throw new ResourceNotFoundException("Credential not found");
        }
        return vault;
    }

    private CredentialResponse toResponse(CredentialVault vault) {
        return new CredentialResponse(
                vault.getId(),
                vault.getName(),
                vault.getCredentialType().name(),
                vault.getKeyVersion(),
                vault.getExpiresAt(),
                vault.getCreatedAt());
    }
}
