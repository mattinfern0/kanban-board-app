package io.mattinfern0.kanbanboardapi.core.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "organization_users")
public class OrganizationMembership {
    @EmbeddedId
    private OrganizationMembershipPk pk;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private OrganizationRole role;

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
