package com.mcpgateway.admin.service;

import com.mcpgateway.admin.dto.ApprovalResponse;
import com.mcpgateway.common.domain.VersionStatus;
import com.mcpgateway.common.exception.ConflictException;
import com.mcpgateway.common.exception.ResourceNotFoundException;
import com.mcpgateway.domain.entity.McpVersion;
import com.mcpgateway.domain.entity.PublishApproval;
import com.mcpgateway.domain.repository.McpVersionRepository;
import com.mcpgateway.domain.repository.PublishApprovalRepository;
import com.mcpgateway.security.SecurityUtils;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApprovalService {

    private final PublishApprovalRepository approvalRepository;
    private final McpVersionRepository versionRepository;
    private final UsageMeteringService usageMeteringService;
    private final AuditService auditService;
    private final ContractNormalizationService normalizationService;

    public ApprovalService(
            PublishApprovalRepository approvalRepository,
            McpVersionRepository versionRepository,
            UsageMeteringService usageMeteringService,
            AuditService auditService,
            ContractNormalizationService normalizationService) {
        this.approvalRepository = approvalRepository;
        this.versionRepository = versionRepository;
        this.usageMeteringService = usageMeteringService;
        this.auditService = auditService;
        this.normalizationService = normalizationService;
    }

    public List<ApprovalResponse> listPending() {
        return approvalRepository.findByStatusOrderByCreatedAtDesc("PENDING").stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ApprovalResponse approve(UUID approvalId, String notes) {
        PublishApproval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new ResourceNotFoundException("Approval not found"));
        if (!"PENDING".equals(approval.getStatus())) {
            throw new ConflictException("Approval is not pending");
        }
        McpVersion version = versionRepository.findById(approval.getVersionId())
                .orElseThrow(() -> new ResourceNotFoundException("Version not found"));
        if (version.getStatus() != VersionStatus.PENDING_APPROVAL) {
            throw new ConflictException("Version is not pending approval");
        }

        approval.setStatus("APPROVED");
        approval.setReviewedBy(SecurityUtils.currentUser().userId());
        approval.setReviewedAt(Instant.now());
        approval.setNotes(notes);
        approvalRepository.save(approval);

        normalizationService.normalizeVersion(version.getId());

        version.setStatus(VersionStatus.PUBLISHED);
        version.setPublishedAt(Instant.now());
        versionRepository.save(version);
        usageMeteringService.record("VERSION_PUBLISHED", "mcp_version", version.getId(), 1);
        auditService.record("VERSION_APPROVED", "mcp_version", version.getId(), notes);

        return toResponse(approval);
    }

    @Transactional
    public PublishApproval requestApproval(UUID versionId) {
        PublishApproval approval = new PublishApproval();
        approval.setVersionId(versionId);
        approval.setRequestedBy(SecurityUtils.currentUser().userId());
        approval.setStatus("PENDING");
        approvalRepository.save(approval);
        auditService.record("VERSION_PUBLISH_REQUESTED", "mcp_version", versionId, null);
        return approval;
    }

    private ApprovalResponse toResponse(PublishApproval approval) {
        return new ApprovalResponse(
                approval.getId(),
                approval.getVersionId(),
                approval.getStatus(),
                approval.getRequestedBy(),
                approval.getReviewedBy(),
                approval.getReviewedAt(),
                approval.getNotes(),
                approval.getCreatedAt());
    }
}
