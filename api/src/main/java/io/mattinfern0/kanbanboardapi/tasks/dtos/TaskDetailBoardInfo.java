package io.mattinfern0.kanbanboardapi.tasks.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TaskDetailBoardInfo(
    UUID id,

    @NotNull
    String title
) {
}
