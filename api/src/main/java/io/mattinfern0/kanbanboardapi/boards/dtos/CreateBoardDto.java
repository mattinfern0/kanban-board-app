package io.mattinfern0.kanbanboardapi.boards.dtos;

import io.mattinfern0.kanbanboardapi.core.constraints.EntityWithIdExists;
import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateBoardDto(
    @NotEmpty
    String title,

    @NotNull
    @EntityWithIdExists(entityClass = Organization.class)
    UUID organizationId
) {
}
