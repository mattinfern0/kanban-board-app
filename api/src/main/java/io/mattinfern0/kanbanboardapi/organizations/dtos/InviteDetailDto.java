package io.mattinfern0.kanbanboardapi.organizations.dtos;

import io.mattinfern0.kanbanboardapi.core.enums.OrganizationInviteStatus;
import jakarta.validation.constraints.Email;

import java.time.ZonedDateTime;
import java.util.UUID;

public record InviteDetailDto(
    UUID id,

    ZonedDateTime createdAt,

    InviteDtoOrganization organization,

    @Email
    String email,

    OrganizationInviteStatus status,

    ZonedDateTime expiresAt,

    String token
) {
}
