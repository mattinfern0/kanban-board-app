package io.mattinfern0.kanbanboardapi.tasks.dtos;

import io.mattinfern0.kanbanboardapi.core.constraints.EntityWithIdExists;
import io.mattinfern0.kanbanboardapi.core.entities.User;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateTaskAssigneeDto(
    @NotNull
    @EntityWithIdExists(entityClass = User.class)
    UUID userId
) {
}
