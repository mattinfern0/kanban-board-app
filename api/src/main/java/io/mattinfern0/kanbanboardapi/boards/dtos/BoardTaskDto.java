package io.mattinfern0.kanbanboardapi.boards.dtos;

import io.mattinfern0.kanbanboardapi.core.enums.TaskStatusCode;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class BoardTaskDto {
    UUID id;

    @NotNull
    String title;

    @NotNull
    String description;

    @NotNull
    TaskStatusCode status;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public TaskStatusCode getStatus() {
        return status;
    }

    public void setStatus(TaskStatusCode status) {
        this.status = status;
    }
}
