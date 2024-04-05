package io.mattinfern0.kanbanboardapi.boards.dtos;

import io.mattinfern0.kanbanboardapi.boards.contraints.BoardColumnOrderTasksOrderOnlyHasAllColumnTasks;
import io.mattinfern0.kanbanboardapi.core.constraints.EntityWithIdExists;
import io.mattinfern0.kanbanboardapi.core.entities.BoardColumn;

import java.util.List;
import java.util.UUID;

@BoardColumnOrderTasksOrderOnlyHasAllColumnTasks
public record BoardColumnTaskReorderDto(
    @EntityWithIdExists(entityClass = BoardColumn.class)
    UUID boardColumnId,

    List<BoardColumnTaskOrderItemDto> newOrder
) {
}
