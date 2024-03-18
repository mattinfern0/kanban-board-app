package io.mattinfern0.kanbanboardapi.boards.dtos;

import io.mattinfern0.kanbanboardapi.boards.contraints.BoardColumnOrderTasksOrderOnlyHasAllColumnTasks;
import io.mattinfern0.kanbanboardapi.core.constraints.EntityWithIdExists;
import io.mattinfern0.kanbanboardapi.core.entities.BoardColumn;

import java.util.List;
import java.util.UUID;

@BoardColumnOrderTasksOrderOnlyHasAllColumnTasks
public class BoardColumnTaskReorderDto {
    @EntityWithIdExists(entityClass = BoardColumn.class)
    UUID boardColumnId;

    List<BoardColumnTaskOrderItemDto> newOrder;

    public UUID getBoardColumnId() {
        return boardColumnId;
    }

    public void setBoardColumnId(UUID boardColumnId) {
        this.boardColumnId = boardColumnId;
    }

    public List<BoardColumnTaskOrderItemDto> getNewOrder() {
        return newOrder;
    }

    public void setNewOrder(List<BoardColumnTaskOrderItemDto> newOrder) {
        this.newOrder = newOrder;
    }
}
