package com.mcpgateway.domain.repository;

import com.mcpgateway.domain.entity.UserRoleEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UserRoleEntity.UserRoleId> {
    List<UserRoleEntity> findByUserId(UUID userId);
}
