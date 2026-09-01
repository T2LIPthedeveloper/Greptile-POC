package com.mcpgateway.admin.controller;

import com.mcpgateway.admin.dto.CreateProviderRequest;
import com.mcpgateway.admin.dto.ProviderResponse;
import com.mcpgateway.admin.dto.UpdateProviderRequest;
import com.mcpgateway.admin.service.ProviderService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/providers")
public class ProviderController {

    private final ProviderService providerService;

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @GetMapping
    public List<ProviderResponse> list() {
        return providerService.listProviders();
    }

    @GetMapping("/{id}")
    public ProviderResponse get(@PathVariable UUID id) {
        return providerService.getProvider(id);
    }

    @PostMapping
    public ProviderResponse create(@Valid @RequestBody CreateProviderRequest request) {
        return providerService.createProvider(request);
    }

    @PutMapping("/{id}")
    public ProviderResponse update(@PathVariable UUID id, @RequestBody UpdateProviderRequest request) {
        return providerService.updateProvider(id, request);
    }
}
