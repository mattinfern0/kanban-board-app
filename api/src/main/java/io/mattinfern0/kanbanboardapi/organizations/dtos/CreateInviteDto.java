package io.mattinfern0.kanbanboardapi.organizations.dtos;

import io.mattinfern0.kanbanboardapi.core.constraints.EntityWithIdExists;
import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateInviteDto(
    @NotNull
    @EntityWithIdExists(entityClass = Organization.class)
    UUID organizationId,

    @NotEmpty
    @Email
    String email
) {
}
