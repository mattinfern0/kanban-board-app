package io.mattinfern0.kanbanboardapi.tasks.dtos;

import io.mattinfern0.kanbanboardapi.core.constraints.EntityWithIdExists;
import io.mattinfern0.kanbanboardapi.core.entities.BoardColumn;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateTaskColumnPositionDTO(
    @NotNull
    @EntityWithIdExists(entityClass = BoardColumn.class, message = "Column with provided ID not found.")
    UUID boardColumnId,

    @Min(value = 0, message = "Column order must be greater than or equal to 0")
    Integer boardColumnOrder
) {
}
