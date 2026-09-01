package com.mcpgateway.admin.controller;

import com.mcpgateway.admin.dto.BootstrapRequest;
import com.mcpgateway.admin.dto.LoginRequest;
import com.mcpgateway.admin.dto.OrganizationResponse;
import com.mcpgateway.admin.dto.RefreshTokenRequest;
import com.mcpgateway.admin.dto.TokenResponse;
import com.mcpgateway.admin.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/organizations/bootstrap")
    public OrganizationResponse bootstrap(@Valid @RequestBody BootstrapRequest request) {
        return authService.bootstrap(request);
    }

    @PostMapping("/auth/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/auth/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request);
    }

    @GetMapping("/organizations/current")
    public OrganizationResponse currentOrganization() {
        return authService.currentOrganization();
    }
}
