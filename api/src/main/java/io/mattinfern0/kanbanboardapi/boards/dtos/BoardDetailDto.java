package io.mattinfern0.kanbanboardapi.boards.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record BoardDetailDto(
    UUID id,

    @NotNull
    UUID organizationId,

    @NotNull
    String title,

    List<BoardColumnDto> boardColumns
) {
}
