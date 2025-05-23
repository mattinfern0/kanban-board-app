package io.mattinfern0.kanbanboardapi.organizations.dtos;

import java.util.UUID;

public record InviteDtoOrganization(
    UUID id,
    String name
) {
}
