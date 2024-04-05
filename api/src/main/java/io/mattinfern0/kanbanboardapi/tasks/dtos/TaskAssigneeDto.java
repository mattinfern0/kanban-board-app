package io.mattinfern0.kanbanboardapi.tasks.dtos;

import org.springframework.lang.Nullable;

import java.util.UUID;

public record TaskAssigneeDto(
    @Nullable
    UUID userId,

    @Nullable
    String firstName,

    @Nullable
    String lastName
) {
}
