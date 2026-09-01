package com.mcpgateway.domain.repository;

import com.mcpgateway.domain.entity.AuthProfile;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthProfileRepository extends JpaRepository<AuthProfile, UUID> {
    List<AuthProfile> findByOrgId(UUID orgId);
}
