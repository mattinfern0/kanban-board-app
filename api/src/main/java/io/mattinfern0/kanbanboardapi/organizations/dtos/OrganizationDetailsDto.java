package io.mattinfern0.kanbanboardapi.organizations.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record OrganizationDetailsDto(
    @NotNull
    UUID id,

    @NotNull
    String displayName,

    @NotNull
    Boolean isPersonal,

    @NotNull
    List<OrganizationMemberDto> members
) {
}
