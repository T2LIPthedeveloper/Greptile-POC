package com.mcpgateway.admin.service;

import com.mcpgateway.admin.dto.BindSkillToolRequest;
import com.mcpgateway.admin.dto.CreateSkillRequest;
import com.mcpgateway.admin.dto.SkillResponse;
import com.mcpgateway.common.exception.ConflictException;
import com.mcpgateway.common.exception.ResourceNotFoundException;
import com.mcpgateway.domain.entity.Skill;
import com.mcpgateway.domain.entity.SkillToolBinding;
import com.mcpgateway.domain.repository.SkillRepository;
import com.mcpgateway.domain.repository.SkillToolBindingRepository;
import com.mcpgateway.security.SecurityUtils;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillToolBindingRepository bindingRepository;
    private final AuditService auditService;

    public SkillService(
            SkillRepository skillRepository,
            SkillToolBindingRepository bindingRepository,
            AuditService auditService) {
        this.skillRepository = skillRepository;
        this.bindingRepository = bindingRepository;
        this.auditService = auditService;
    }

    public List<SkillResponse> list() {
        UUID orgId = SecurityUtils.currentUser().orgId();
        return skillRepository.findByOrgId(orgId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public SkillResponse create(CreateSkillRequest request) {
        UUID orgId = SecurityUtils.currentUser().orgId();
        if (skillRepository.findByOrgIdAndSlug(orgId, request.slug()).isPresent()) {
            throw new ConflictException("Skill slug already exists");
        }
        Skill skill = new Skill();
        skill.setOrgId(orgId);
        skill.setSlug(request.slug());
        skill.setDisplayName(request.displayName());
        skill.setDescription(request.description());
        skill.setDefinition(request.definition());
        skillRepository.save(skill);
        auditService.record("SKILL_CREATED", "skill", skill.getId(), request.slug());
        return toResponse(skill);
    }

    @Transactional
    public void bindTool(UUID skillId, BindSkillToolRequest request) {
        Skill skill = findForOrg(skillId);
        SkillToolBinding binding = new SkillToolBinding();
        binding.setSkillId(skill.getId());
        binding.setToolName(request.toolName());
        binding.setVersionId(request.versionId());
        bindingRepository.save(binding);
    }

    public Map<String, Object> invoke(UUID skillId, Map<String, Object> input) {
        Skill skill = findForOrg(skillId);
        List<SkillToolBinding> bindings = bindingRepository.findBySkillId(skillId);
        return Map.of(
                "skill", skill.getSlug(),
                "input", input,
                "boundTools", bindings.stream().map(SkillToolBinding::getToolName).toList(),
                "result", "Skill invocation stub — bind tools for runtime in Phase E");
    }

    private Skill findForOrg(UUID skillId) {
        UUID orgId = SecurityUtils.currentUser().orgId();
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
        if (!skill.getOrgId().equals(orgId)) {
            throw new ResourceNotFoundException("Skill not found");
        }
        return skill;
    }

    private SkillResponse toResponse(Skill skill) {
        return new SkillResponse(
                skill.getId(),
                skill.getSlug(),
                skill.getDisplayName(),
                skill.getDescription(),
                skill.getDefinition(),
                skill.getStatus(),
                skill.getCreatedAt());
    }
}
