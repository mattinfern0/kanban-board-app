package io.mattinfern0.kanbanboardapi.tasks.dtos;

import io.mattinfern0.kanbanboardapi.core.constraints.EntityWithIdExists;
import io.mattinfern0.kanbanboardapi.core.constraints.TaskColumnAndStatusComboValid;
import io.mattinfern0.kanbanboardapi.core.entities.BoardColumn;
import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.enums.TaskStatusCode;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@TaskColumnAndStatusComboValid
public class CreateTaskDto {
    @NotNull
    @EntityWithIdExists(entityClass = Organization.class)
    UUID organizationId;

    @NotNull
    String title;

    @NotNull
    String description;

    @Nullable
    @EntityWithIdExists(entityClass = BoardColumn.class)
    UUID boardColumnId;

    @Nullable
    TaskStatusCode status;

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Nullable
    public UUID getBoardColumnId() {
        return boardColumnId;
    }

    public void setBoardColumnId(@Nullable UUID boardColumnId) {
        this.boardColumnId = boardColumnId;
    }

    @Nullable
    public TaskStatusCode getStatus() {
        return status;
    }

    public void setStatus(@Nullable TaskStatusCode status) {
        this.status = status;
    }
}
