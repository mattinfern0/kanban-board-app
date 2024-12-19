package io.mattinfern0.kanbanboardapi.tasks.dtos;

import io.mattinfern0.kanbanboardapi.core.enums.TaskPriority;
import io.mattinfern0.kanbanboardapi.core.enums.TaskStatusCode;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record TaskDetailDto(
    UUID id,

    ZonedDateTime createdAt,
    @NotNull
    UUID organizationId,

    @NotNull
    String title,

    @NotNull
    String description,

    @Nullable
    UUID boardColumnId,

    TaskDetailBoardInfo board,

    @Nullable
    Integer boardColumnOrder,

    TaskStatusCode status,

    @Nullable
    TaskPriority priority,

    List<TaskAssigneeDto> assignees
) {
}
