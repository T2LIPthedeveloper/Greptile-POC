package com.mcpgateway.domain.repository;

import com.mcpgateway.domain.entity.VersionCredentialEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VersionCredentialRepository extends JpaRepository<VersionCredentialEntity, VersionCredentialEntity.VersionCredentialId> {
    List<VersionCredentialEntity> findByVersionId(UUID versionId);
}
