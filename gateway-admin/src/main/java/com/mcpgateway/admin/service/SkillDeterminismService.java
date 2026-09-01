package com.mcpgateway.admin.service;

import com.mcpgateway.domain.entity.SkillInvocationLedger;
import com.mcpgateway.domain.repository.SkillInvocationLedgerRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SkillDeterminismService {

    private final SkillInvocationLedgerRepository ledgerRepository;

    public SkillDeterminismService(SkillInvocationLedgerRepository ledgerRepository) {
        this.ledgerRepository = ledgerRepository;
    }

    @Transactional
    public Map<String, Object> recordOrReplay(
            UUID skillId, String idempotencyKey, String deterministicSeed, Map<String, Object> input) {
        var existing = ledgerRepository.findBySkillIdAndIdempotencyKey(skillId, idempotencyKey);
        if (existing.isPresent()) {
            SkillInvocationLedger entry = existing.get();
            return Map.of(
                    "replayed", true,
                    "inputHash", entry.getInputHash(),
                    "outputHash", entry.getOutputHash(),
                    "deterministicSeed", entry.getDeterministicSeed());
        }
        String inputHash = hash(input.toString());
        String outputHash = hash(deterministicSeed + ":" + inputHash);
        SkillInvocationLedger entry = new SkillInvocationLedger();
        entry.setSkillId(skillId);
        entry.setIdempotencyKey(idempotencyKey);
        entry.setDeterministicSeed(deterministicSeed);
        entry.setInputHash(inputHash);
        entry.setOutputHash(outputHash);
        ledgerRepository.save(entry);
        return Map.of(
                "replayed", false,
                "inputHash", inputHash,
                "outputHash", outputHash,
                "deterministicSeed", deterministicSeed);
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            return value;
        }
    }
}
