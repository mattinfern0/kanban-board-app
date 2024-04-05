package io.mattinfern0.kanbanboardapi.tasks.dtos;

import io.mattinfern0.kanbanboardapi.core.enums.TaskStatusCode;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;
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

    TaskStatusCode status
) {
}
