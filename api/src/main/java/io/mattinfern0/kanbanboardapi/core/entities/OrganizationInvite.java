package io.mattinfern0.kanbanboardapi.core.entities;

import io.mattinfern0.kanbanboardapi.core.enums.OrganizationInviteStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "organization_invites")
public class OrganizationInvite {
    @Id
    @GeneratedValue
    UUID id;

    @CreationTimestamp
    ZonedDateTime createdAt;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "organization_id")
    Organization organization;

    @Column(nullable = false, name = "status_id")
    OrganizationInviteStatus status;

    @Column(name = "email")
    @Email
    String email;

    @Column(name = "token", unique = true)
    @NotEmpty
    String token;

    @NotNull
    ZonedDateTime expiresAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ZonedDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(ZonedDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public OrganizationInviteStatus getStatus() {
        return status;
    }

    public void setStatus(OrganizationInviteStatus status) {
        this.status = status;
    }
}
