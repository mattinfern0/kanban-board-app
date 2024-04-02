package io.mattinfern0.kanbanboardapi.core.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class OrganizationMembershipPk implements Serializable {
    private UUID userId;
    private UUID organizationId;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrganizationMembershipPk that = (OrganizationMembershipPk) o;

        if (!userId.equals(that.userId)) return false;
        return organizationId.equals(that.organizationId);
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + organizationId.hashCode();
        return result;
    }
}
