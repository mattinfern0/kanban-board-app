package io.mattinfern0.kanbanboardapi.tasks.dtos;

import io.mattinfern0.kanbanboardapi.core.constraints.EntityWithIdExists;
import io.mattinfern0.kanbanboardapi.core.entities.BoardColumn;
import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.enums.TaskStatusCode;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateUpdateTaskDto(
    @NotNull
    @EntityWithIdExists(entityClass = Organization.class)
    UUID organizationId,

    @NotNull
    String title,

    @NotNull
    String description,

    @Nullable
    @EntityWithIdExists(entityClass = BoardColumn.class)
    UUID boardColumnId,

    // If column and status are both not null, system should prioritize column's status
    @Nullable
    TaskStatusCode status
) {
}
