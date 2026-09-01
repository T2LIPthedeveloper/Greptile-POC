package com.mcpgateway.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "federation_peers")
public class FederationPeer {

    @Id
    private UUID id;

    @Column(name = "org_id", nullable = false)
    private UUID orgId;

    @Column(nullable = false, length = 64)
    private String slug;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "peer_url", nullable = false)
    private String peerUrl;

    @Column(name = "trust_level", nullable = false, length = 32)
    private String trustLevel;

    @Column(nullable = false, length = 32)
    private String status;

    @Column(name = "last_health_status", length = 32)
    private String lastHealthStatus;

    @Column(name = "last_health_at")
    private Instant lastHealthAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (id == null) id = UUID.randomUUID();
        createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getOrgId() { return orgId; }
    public void setOrgId(UUID orgId) { this.orgId = orgId; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getPeerUrl() { return peerUrl; }
    public void setPeerUrl(String peerUrl) { this.peerUrl = peerUrl; }
    public String getTrustLevel() { return trustLevel; }
    public void setTrustLevel(String trustLevel) { this.trustLevel = trustLevel; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getLastHealthStatus() { return lastHealthStatus; }
    public void setLastHealthStatus(String lastHealthStatus) { this.lastHealthStatus = lastHealthStatus; }
    public Instant getLastHealthAt() { return lastHealthAt; }
    public void setLastHealthAt(Instant lastHealthAt) { this.lastHealthAt = lastHealthAt; }
    public Instant getCreatedAt() { return createdAt; }
}
