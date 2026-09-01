package com.mcpgateway.admin.service;

import com.mcpgateway.admin.dto.AuthProfileResponse;
import com.mcpgateway.admin.dto.CreateAuthProfileRequest;
import com.mcpgateway.domain.entity.AuthProfile;
import com.mcpgateway.domain.repository.AuthProfileRepository;
import com.mcpgateway.security.SecurityUtils;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthProfileService {

    private final AuthProfileRepository authProfileRepository;

    public AuthProfileService(AuthProfileRepository authProfileRepository) {
        this.authProfileRepository = authProfileRepository;
    }

    public List<AuthProfileResponse> list() {
        UUID orgId = SecurityUtils.currentUser().orgId();
        return authProfileRepository.findByOrgId(orgId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public AuthProfileResponse create(CreateAuthProfileRequest request) {
        AuthProfile profile = new AuthProfile();
        profile.setOrgId(SecurityUtils.currentUser().orgId());
        profile.setName(request.name());
        profile.setAuthMethod(request.authMethod());
        profile.setConfig(request.config() != null ? request.config() : "{}");
        profile.setCredentialId(request.credentialId());
        authProfileRepository.save(profile);
        return toResponse(profile);
    }

    private AuthProfileResponse toResponse(AuthProfile profile) {
        return new AuthProfileResponse(
                profile.getId(),
                profile.getName(),
                profile.getAuthMethod(),
                profile.getConfig(),
                profile.getCredentialId(),
                profile.getCreatedAt());
    }
}
