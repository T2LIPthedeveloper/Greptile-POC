package com.mcpgateway.domain.entity;

import com.mcpgateway.common.domain.UserRole;
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
@Table(name = "user_roles")
@IdClass(UserRoleEntity.UserRoleId.class)
public class UserRoleEntity {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Id
    @Column(name = "org_id")
    private UUID orgId;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private UserRole role;

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getOrgId() { return orgId; }
    public void setOrgId(UUID orgId) { this.orgId = orgId; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public static class UserRoleId implements Serializable {
        private UUID userId;
        private UUID orgId;
        private UserRole role;

        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }
        public UUID getOrgId() { return orgId; }
        public void setOrgId(UUID orgId) { this.orgId = orgId; }
        public UserRole getRole() { return role; }
        public void setRole(UserRole role) { this.role = role; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserRoleId that = (UserRoleId) o;
            return userId.equals(that.userId) && orgId.equals(that.orgId) && role == that.role;
        }

        @Override
        public int hashCode() {
            return userId.hashCode() * 31 + orgId.hashCode() * 31 + role.hashCode();
        }
    }
}
