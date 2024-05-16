package io.mattinfern0.kanbanboardapi.tasks.dtos;

import io.mattinfern0.kanbanboardapi.core.constraints.EntityWithIdExists;
import io.mattinfern0.kanbanboardapi.core.entities.BoardColumn;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateTaskColumnPositionDTO(
    @NotNull
    @EntityWithIdExists(entityClass = BoardColumn.class)
    UUID boardColumnId,

    @NotNull
    @Min(0)
    Integer boardColumnOrder
) {
}
