package com.mcpgateway.admin.service;

import com.mcpgateway.admin.dto.BootstrapRequest;
import com.mcpgateway.admin.dto.LoginRequest;
import com.mcpgateway.admin.dto.OrganizationResponse;
import com.mcpgateway.admin.dto.RefreshTokenRequest;
import com.mcpgateway.admin.dto.TokenResponse;
import com.mcpgateway.common.domain.OrgStatus;
import com.mcpgateway.common.domain.UserRole;
import com.mcpgateway.common.domain.UserStatus;
import com.mcpgateway.common.exception.ConflictException;
import com.mcpgateway.domain.entity.Organization;
import com.mcpgateway.domain.entity.User;
import com.mcpgateway.domain.entity.UserRoleEntity;
import com.mcpgateway.domain.repository.OrganizationRepository;
import com.mcpgateway.domain.repository.UserRepository;
import com.mcpgateway.domain.repository.UserRoleRepository;
import com.mcpgateway.security.AuthenticatedUser;
import com.mcpgateway.security.JwtTokenService;
import com.mcpgateway.security.SecurityUtils;
import java.util.List;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthService(
            UserRepository userRepository,
            OrganizationRepository organizationRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public OrganizationResponse bootstrap(BootstrapRequest request) {
        if (organizationRepository.findBySlug(request.orgSlug()).isPresent()) {
            throw new ConflictException("Organization slug already exists");
        }
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ConflictException("Email already registered");
        }

        Organization org = new Organization();
        org.setSlug(request.orgSlug());
        org.setName(request.orgName());
        org.setStatus(OrgStatus.ACTIVE);
        organizationRepository.save(org);

        User user = new User();
        user.setOrgId(org.getId());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        UserRoleEntity role = new UserRoleEntity();
        role.setUserId(user.getId());
        role.setOrgId(org.getId());
        role.setRole(UserRole.ORG_ADMIN);
        userRoleRepository.save(role);

        return new OrganizationResponse(org.getId(), org.getSlug(), org.getName(), org.getStatus().name());
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        AuthenticatedUser authUser = toAuthenticatedUser(user);
        return buildTokenResponse(authUser);
    }

    public TokenResponse refresh(RefreshTokenRequest request) {
        AuthenticatedUser user = jwtTokenService.parseRefreshToken(request.refreshToken());
        return buildTokenResponse(user);
    }

    public OrganizationResponse currentOrganization() {
        AuthenticatedUser user = SecurityUtils.currentUser();
        Organization org = organizationRepository.findById(user.orgId())
                .orElseThrow(() -> new IllegalStateException("Organization not found"));
        return new OrganizationResponse(org.getId(), org.getSlug(), org.getName(), org.getStatus().name());
    }

    private AuthenticatedUser toAuthenticatedUser(User user) {
        List<String> roles = userRoleRepository.findByUserId(user.getId()).stream()
                .map(r -> r.getRole().name())
                .toList();
        return new AuthenticatedUser(user.getId(), user.getOrgId(), user.getEmail(), roles);
    }

    private TokenResponse buildTokenResponse(AuthenticatedUser user) {
        return new TokenResponse(
                jwtTokenService.createAccessToken(user),
                jwtTokenService.createRefreshToken(user),
                3600);
    }
}
