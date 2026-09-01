package com.mcpgateway.admin.service;

import com.mcpgateway.admin.dto.CreateFederationPeerRequest;
import com.mcpgateway.admin.dto.FederationPeerResponse;
import com.mcpgateway.common.exception.ConflictException;
import com.mcpgateway.common.exception.ResourceNotFoundException;
import com.mcpgateway.domain.entity.FederationPeer;
import com.mcpgateway.domain.repository.FederationPeerRepository;
import com.mcpgateway.security.SecurityUtils;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class FederationPeerService {

    private final FederationPeerRepository peerRepository;
    private final WebClient webClient;

    public FederationPeerService(FederationPeerRepository peerRepository, WebClient.Builder webClientBuilder) {
        this.peerRepository = peerRepository;
        this.webClient = webClientBuilder.build();
    }

    public List<FederationPeerResponse> list() {
        UUID orgId = SecurityUtils.currentUser().orgId();
        return peerRepository.findByOrgId(orgId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public FederationPeerResponse create(CreateFederationPeerRequest request) {
        UUID orgId = SecurityUtils.currentUser().orgId();
        if (peerRepository.findByOrgIdAndSlug(orgId, request.slug()).isPresent()) {
            throw new ConflictException("Peer slug already exists");
        }
        FederationPeer peer = new FederationPeer();
        peer.setOrgId(orgId);
        peer.setSlug(request.slug());
        peer.setDisplayName(request.displayName());
        peer.setPeerUrl(request.peerUrl());
        peer.setTrustLevel("READ_ONLY");
        peer.setStatus("ACTIVE");
        peerRepository.save(peer);
        return toResponse(peer);
    }

    @Transactional
    public FederationPeerResponse probe(UUID id) {
        FederationPeer peer = findForOrg(id);
        try {
            webClient.get().uri(peer.getPeerUrl() + "/mcp/health").retrieve().toBodilessEntity().block();
            peer.setLastHealthStatus("UP");
        } catch (Exception e) {
            peer.setLastHealthStatus("DOWN");
        }
        peer.setLastHealthAt(Instant.now());
        peerRepository.save(peer);
        return toResponse(peer);
    }

    private FederationPeer findForOrg(UUID id) {
        UUID orgId = SecurityUtils.currentUser().orgId();
        FederationPeer peer = peerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Peer not found"));
        if (!peer.getOrgId().equals(orgId)) {
            throw new ResourceNotFoundException("Peer not found");
        }
        return peer;
    }

    private FederationPeerResponse toResponse(FederationPeer peer) {
        return new FederationPeerResponse(
                peer.getId(), peer.getSlug(), peer.getDisplayName(), peer.getPeerUrl(),
                peer.getTrustLevel(), peer.getStatus(), peer.getLastHealthStatus(), peer.getLastHealthAt());
    }
}
