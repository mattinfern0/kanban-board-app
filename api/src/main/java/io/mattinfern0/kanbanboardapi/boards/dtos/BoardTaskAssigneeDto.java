package io.mattinfern0.kanbanboardapi.boards.dtos;

import org.springframework.lang.Nullable;

import java.util.UUID;

public record BoardTaskAssigneeDto(
    @Nullable
    UUID userId,

    @Nullable
    String firstName,

    @Nullable
    String lastName
) {
}
