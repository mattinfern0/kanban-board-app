package io.mattinfern0.kanbanboardapi.core.entities;

import io.mattinfern0.kanbanboardapi.core.enums.OrganizationRole;
import jakarta.persistence.*;

@Entity
@Table(name = "organization_users")
public class OrganizationMembership {
    @EmbeddedId
    private OrganizationMembershipPk pk;

    @Column(name = "role_id")
    OrganizationRole role;

    @ManyToOne()
    @MapsId("userId")
    User user;

    @ManyToOne()
    @MapsId("organizationId")
    Organization organization;

    public OrganizationMembership() {
    }

    public OrganizationMembership(
        Organization organization,
        User user
    ) {
        this.user = user;
        this.organization = organization;
        this.pk = new OrganizationMembershipPk(
            organization.getId(),
            user.getId()
        );
    }

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
