package io.mattinfern0.kanbanboardapi.users.dtos;

import java.util.UUID;

public record UserDetailOrganizationListItem(
    UUID id,
    String displayName
) {
}
