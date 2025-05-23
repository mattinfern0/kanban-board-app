package io.mattinfern0.kanbanboardapi.organizations.dtos;

import io.mattinfern0.kanbanboardapi.core.constraints.EntityWithIdExists;
import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import jakarta.validation.constraints.Email;

import java.util.UUID;

public record CreateInviteDto(
    @EntityWithIdExists(entityClass = Organization.class)
    UUID organizationId,

    @Email
    String email
) {
}
