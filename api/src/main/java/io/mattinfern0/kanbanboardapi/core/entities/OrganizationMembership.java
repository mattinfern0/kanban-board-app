package io.mattinfern0.kanbanboardapi.core.entities;

import io.mattinfern0.kanbanboardapi.core.enums.OrganizationRole;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "organization_users")
public class OrganizationMembership {
    @EmbeddedId
    private OrganizationMembershipPk pk;

    @Column(name = "role_id")
    OrganizationRole role;

    public OrganizationMembershipPk getPk() {
        return pk;
    }

    public void setPk(OrganizationMembershipPk pk) {
        this.pk = pk;
    }

    public OrganizationRole getRole() {
        return role;
    }

    public void setRole(OrganizationRole role) {
        this.role = role;
    }
}
