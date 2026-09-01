package com.mcpgateway.admin.service;

import com.mcpgateway.admin.dto.ContractDiffResponse;
import com.mcpgateway.common.exception.ResourceNotFoundException;
import com.mcpgateway.domain.entity.McpToolContract;
import com.mcpgateway.domain.repository.McpToolContractRepository;
import com.mcpgateway.domain.repository.McpVersionRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ContractDiffService {

    private final McpVersionRepository versionRepository;
    private final McpToolContractRepository toolContractRepository;

    public ContractDiffService(
            McpVersionRepository versionRepository,
            McpToolContractRepository toolContractRepository) {
        this.versionRepository = versionRepository;
        this.toolContractRepository = toolContractRepository;
    }

    public ContractDiffResponse diff(UUID baseVersionId, UUID targetVersionId) {
        if (!versionRepository.existsById(baseVersionId) || !versionRepository.existsById(targetVersionId)) {
            throw new ResourceNotFoundException("Version not found");
        }

        Map<String, McpToolContract> baseTools = toolContractRepository.findByVersionId(baseVersionId).stream()
                .collect(Collectors.toMap(McpToolContract::getToolName, Function.identity()));
        Map<String, McpToolContract> targetTools = toolContractRepository.findByVersionId(targetVersionId).stream()
                .collect(Collectors.toMap(McpToolContract::getToolName, Function.identity()));

        Set<String> baseNames = new HashSet<>(baseTools.keySet());
        Set<String> targetNames = new HashSet<>(targetTools.keySet());

        List<String> added = new ArrayList<>();
        List<String> removed = new ArrayList<>();
        List<String> changed = new ArrayList<>();

        for (String name : targetNames) {
            if (!baseNames.contains(name)) {
                added.add(name);
            } else {
                McpToolContract base = baseTools.get(name);
                McpToolContract target = targetTools.get(name);
                if (!base.getInputSchema().equals(target.getInputSchema())) {
                    changed.add(name);
                }
            }
        }
        for (String name : baseNames) {
            if (!targetNames.contains(name)) {
                removed.add(name);
            }
        }

        return new ContractDiffResponse(added, removed, changed);
    }
}
