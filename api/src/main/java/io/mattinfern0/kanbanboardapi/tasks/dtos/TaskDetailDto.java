package io.mattinfern0.kanbanboardapi.tasks.dtos;

import io.mattinfern0.kanbanboardapi.core.enums.TaskStatusCode;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.UUID;

public class TaskDetailDto {

    UUID id;

    ZonedDateTime createdAt;
    @NotNull
    UUID organizationId;

    @NotNull
    String title;

    @NotNull
    String description;

    @Nullable
    UUID boardColumnId;

    TaskStatusCode status;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

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

    public void setBoardColumnId(@Nullable UUID boardColumn) {
        this.boardColumnId = boardColumn;
    }

    @NotNull
    public TaskStatusCode getStatus() {
        return status;
    }

    public void setStatus(@Nullable TaskStatusCode status) {
        this.status = status;
    }
}
