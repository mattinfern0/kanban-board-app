package io.mattinfern0.kanbanboardapi.organizations.dtos;

import io.mattinfern0.kanbanboardapi.core.enums.OrganizationRole;
import jakarta.validation.constraints.NotNull;

public record UpdateMembershipDto(
    @NotNull
    OrganizationRole role
) {
}
