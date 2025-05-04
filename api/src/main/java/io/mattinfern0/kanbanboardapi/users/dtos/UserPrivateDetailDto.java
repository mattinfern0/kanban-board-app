package io.mattinfern0.kanbanboardapi.users.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record UserPrivateDetailDto(
    @NotNull
    UUID id,

    @NotEmpty
    String firstName,

    @NotEmpty
    String lastName,

    @NotEmpty
    UUID personalOrganizationId,

    List<UserDetailOrganizationListItem> organizations
) {
}
