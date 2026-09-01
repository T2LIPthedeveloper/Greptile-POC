package com.mcpgateway.domain.repository;

import com.mcpgateway.domain.entity.SkillInvocationLedger;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillInvocationLedgerRepository extends JpaRepository<SkillInvocationLedger, UUID> {
    Optional<SkillInvocationLedger> findBySkillIdAndIdempotencyKey(UUID skillId, String idempotencyKey);
}
