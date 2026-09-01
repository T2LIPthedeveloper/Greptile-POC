package com.mcpgateway.domain.repository;

import com.mcpgateway.domain.entity.CredentialVault;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredentialVaultRepository extends JpaRepository<CredentialVault, UUID> {
    List<CredentialVault> findByOrgId(UUID orgId);
}
