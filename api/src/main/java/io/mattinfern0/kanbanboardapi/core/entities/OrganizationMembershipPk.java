package io.mattinfern0.kanbanboardapi.core.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class OrganizationMembershipPk implements Serializable {
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "organization_id")
    private UUID organizationId;

    public OrganizationMembershipPk() {
    }

    public OrganizationMembershipPk(UUID organizationId, UUID userId) {
        this.organizationId = organizationId;
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getOrganizationId() {
        return organizationId;
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
