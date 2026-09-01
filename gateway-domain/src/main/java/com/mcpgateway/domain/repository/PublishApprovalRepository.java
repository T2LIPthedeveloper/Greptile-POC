package com.mcpgateway.domain.repository;

import com.mcpgateway.domain.entity.PublishApproval;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublishApprovalRepository extends JpaRepository<PublishApproval, UUID> {
    List<PublishApproval> findByStatusOrderByCreatedAtDesc(String status);
    List<PublishApproval> findByVersionId(UUID versionId);
}
