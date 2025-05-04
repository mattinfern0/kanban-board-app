package io.mattinfern0.kanbanboardapi.organizations.dtos;

import io.mattinfern0.kanbanboardapi.core.enums.OrganizationRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrganizationMemberDto(
    @NotNull
    UUID userId,

    @NotEmpty
    String firstName,

    @NotEmpty
    String lastName,

    @NotNull
    OrganizationRole role
) {
}
