package io.mattinfern0.kanbanboardapi.boards.dtos;

import io.mattinfern0.kanbanboardapi.core.constraints.EntityWithIdExists;
import io.mattinfern0.kanbanboardapi.core.entities.Task;

import java.util.UUID;

public record BoardColumnTaskOrderItemDto(
    @EntityWithIdExists(entityClass = Task.class)
    UUID taskId
) {
}
