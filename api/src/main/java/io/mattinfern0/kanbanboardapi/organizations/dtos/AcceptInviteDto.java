package io.mattinfern0.kanbanboardapi.organizations.dtos;

import jakarta.validation.constraints.NotEmpty;

public record AcceptInviteDto(
    @NotEmpty
    String token
) {
}
