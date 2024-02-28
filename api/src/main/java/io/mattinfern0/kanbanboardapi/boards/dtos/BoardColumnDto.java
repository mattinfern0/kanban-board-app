package io.mattinfern0.kanbanboardapi.boards.dtos;

import io.mattinfern0.kanbanboardapi.core.enums.TaskStatusCode;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public class BoardColumnDto {
    UUID id;

    @NotNull
    String title;

    @NotNull
    TaskStatusCode taskStatus;

    List<BoardTaskDto> tasks;

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

    public List<BoardTaskDto> getTasks() {
        return tasks;
    }

    public void setTasks(List<BoardTaskDto> tasks) {
        this.tasks = tasks;
    }

    public TaskStatusCode getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatusCode taskStatus) {
        this.taskStatus = taskStatus;
    }
}
