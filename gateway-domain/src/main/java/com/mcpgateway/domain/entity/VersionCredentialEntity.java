package com.mcpgateway.domain.entity;

import com.mcpgateway.common.domain.CredentialUsage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "version_credentials")
@IdClass(VersionCredentialEntity.VersionCredentialId.class)
public class VersionCredentialEntity {

    @Id
    @Column(name = "version_id")
    private UUID versionId;

    @Id
    @Column(name = "credential_id")
    private UUID credentialId;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private CredentialUsage usage;

    public UUID getVersionId() { return versionId; }
    public void setVersionId(UUID versionId) { this.versionId = versionId; }
    public UUID getCredentialId() { return credentialId; }
    public void setCredentialId(UUID credentialId) { this.credentialId = credentialId; }
    public CredentialUsage getUsage() { return usage; }
    public void setUsage(CredentialUsage usage) { this.usage = usage; }

    public static class VersionCredentialId implements Serializable {
        private UUID versionId;
        private UUID credentialId;
        private CredentialUsage usage;

        public UUID getVersionId() { return versionId; }
        public void setVersionId(UUID versionId) { this.versionId = versionId; }
        public UUID getCredentialId() { return credentialId; }
        public void setCredentialId(UUID credentialId) { this.credentialId = credentialId; }
        public CredentialUsage getUsage() { return usage; }
        public void setUsage(CredentialUsage usage) { this.usage = usage; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            VersionCredentialId that = (VersionCredentialId) o;
            return versionId.equals(that.versionId) && credentialId.equals(that.credentialId) && usage == that.usage;
        }

        @Override
        public int hashCode() {
            return versionId.hashCode() * 31 + credentialId.hashCode() * 31 + usage.hashCode();
        }
    }
}
