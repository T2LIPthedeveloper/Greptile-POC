package com.mcpgateway.admin.controller;

import com.mcpgateway.admin.dto.CreateFederationPeerRequest;
import com.mcpgateway.admin.dto.FederationPeerResponse;
import com.mcpgateway.admin.service.FederationPeerService;
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
@RequestMapping("/api/v1/federation-peers")
public class FederationPeerController {

    private final FederationPeerService peerService;

    public FederationPeerController(FederationPeerService peerService) {
        this.peerService = peerService;
    }

    @GetMapping
    public List<FederationPeerResponse> list() {
        return peerService.list();
    }

    @PostMapping
    public FederationPeerResponse create(@Valid @RequestBody CreateFederationPeerRequest request) {
        return peerService.create(request);
    }

    @PostMapping("/{id}/probe")
    public FederationPeerResponse probe(@PathVariable UUID id) {
        return peerService.probe(id);
    }
}
