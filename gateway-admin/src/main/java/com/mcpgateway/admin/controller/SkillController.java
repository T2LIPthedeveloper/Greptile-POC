package com.mcpgateway.admin.controller;

import com.mcpgateway.admin.dto.BindSkillToolRequest;
import com.mcpgateway.admin.dto.CreateSkillRequest;
import com.mcpgateway.admin.dto.SkillResponse;
import com.mcpgateway.admin.service.SkillService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    public List<SkillResponse> list() {
        return skillService.list();
    }

    @PostMapping
    public SkillResponse create(@Valid @RequestBody CreateSkillRequest request) {
        return skillService.create(request);
    }

    @PostMapping("/{id}/bindings")
    public void bindTool(@PathVariable UUID id, @Valid @RequestBody BindSkillToolRequest request) {
        skillService.bindTool(id, request);
    }

    @PostMapping("/{id}/invoke")
    public Map<String, Object> invoke(@PathVariable UUID id, @RequestBody Map<String, Object> input) {
        return skillService.invoke(id, input);
    }
}
