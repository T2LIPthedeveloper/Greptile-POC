package com.mcpgateway.proxy;

import com.mcpgateway.common.exception.ResourceNotFoundException;
import com.mcpgateway.domain.entity.FederationPeer;
import com.mcpgateway.domain.repository.FederationPeerRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class FederationRouter {

    private final FederationPeerRepository peerRepository;

    public FederationRouter(FederationPeerRepository peerRepository) {
        this.peerRepository = peerRepository;
    }

    public String resolveUpstreamUrl(UUID orgId, String baseUrl) {
        if (baseUrl == null || !baseUrl.startsWith("peer://")) {
            return baseUrl;
        }
        String remainder = baseUrl.substring("peer://".length());
        int slash = remainder.indexOf('/');
        String peerSlug = slash >= 0 ? remainder.substring(0, slash) : remainder;
        String path = slash >= 0 ? remainder.substring(slash) : "/mcp";
        FederationPeer peer = peerRepository.findByOrgIdAndSlug(orgId, peerSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Federation peer not found: " + peerSlug));
        String peerBase = peer.getPeerUrl().replaceAll("/$", "");
        return peerBase + path;
    }
}
