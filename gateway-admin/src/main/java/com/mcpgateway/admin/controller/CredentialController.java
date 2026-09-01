package com.mcpgateway.admin.controller;

import com.mcpgateway.admin.dto.CreateCredentialRequest;
import com.mcpgateway.admin.dto.CredentialResponse;
import com.mcpgateway.admin.service.CredentialService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/credentials")
public class CredentialController {

    private final CredentialService credentialService;

    public CredentialController(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    @GetMapping
    public List<CredentialResponse> list() {
        return credentialService.listCredentials();
    }

    @GetMapping("/{id}")
    public CredentialResponse get(@PathVariable UUID id) {
        return credentialService.getCredential(id);
    }

    @PostMapping
    public CredentialResponse create(@Valid @RequestBody CreateCredentialRequest request) {
        return credentialService.createCredential(request);
    }
}
