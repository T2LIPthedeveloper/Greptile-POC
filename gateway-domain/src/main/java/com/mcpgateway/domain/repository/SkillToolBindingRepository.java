package com.mcpgateway.domain.repository;

import com.mcpgateway.domain.entity.SkillToolBinding;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillToolBindingRepository extends JpaRepository<SkillToolBinding, UUID> {
    List<SkillToolBinding> findBySkillId(UUID skillId);
}
