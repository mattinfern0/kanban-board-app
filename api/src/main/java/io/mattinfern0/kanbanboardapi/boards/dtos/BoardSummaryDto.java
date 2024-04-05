package io.mattinfern0.kanbanboardapi.boards.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BoardSummaryDto(
    UUID id,

    @NotNull
    UUID organizationId,

    @NotNull
    String title
) {
}
