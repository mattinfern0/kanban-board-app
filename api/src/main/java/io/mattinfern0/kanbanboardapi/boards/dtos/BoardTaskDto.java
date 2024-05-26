package io.mattinfern0.kanbanboardapi.boards.dtos;

import io.mattinfern0.kanbanboardapi.core.enums.TaskStatusCode;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record BoardTaskDto(
    UUID id,

    @NotNull
    String title,

    @NotNull
    String description,

    @NotNull
    TaskStatusCode status,

    List<BoardTaskAssigneeDto> assignees
) {
}
