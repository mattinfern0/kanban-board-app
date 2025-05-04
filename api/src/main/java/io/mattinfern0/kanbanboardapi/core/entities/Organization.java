package io.mattinfern0.kanbanboardapi.core.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "organization")
public class Organization {
    @Id
    @GeneratedValue
    UUID id;

    @NotNull
    @Column(name = "display_name")
    String displayName;

    @OneToOne
    @JoinColumn(name = "personal_for_user_id")
    User personalForUser;

    @OneToMany(mappedBy = "organization")
    List<OrganizationMembership> memberships;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public User getPersonalForUser() {
        return personalForUser;
    }

    public void setPersonalForUser(User personalForUser) {
        this.personalForUser = personalForUser;
    }

    public boolean getIsPersonal() {
        return personalForUser != null;
    }

    public List<OrganizationMembership> getMemberships() {
        return memberships;
    }

    public void setMemberships(List<OrganizationMembership> memberships) {
        this.memberships = memberships;
    }
}
