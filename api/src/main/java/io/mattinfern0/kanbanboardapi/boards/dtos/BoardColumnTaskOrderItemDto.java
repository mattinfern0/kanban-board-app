package io.mattinfern0.kanbanboardapi.boards.dtos;

import io.mattinfern0.kanbanboardapi.boards.contraints.BoardColumnOrderTasksOrderOnlyHasAllColumnTasks;
import io.mattinfern0.kanbanboardapi.core.constraints.EntityWithIdExists;
import io.mattinfern0.kanbanboardapi.core.entities.Task;

import java.util.UUID;

@BoardColumnOrderTasksOrderOnlyHasAllColumnTasks
public class BoardColumnTaskOrderItemDto {
    @EntityWithIdExists(entityClass = Task.class)
    UUID taskId;

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }
}
