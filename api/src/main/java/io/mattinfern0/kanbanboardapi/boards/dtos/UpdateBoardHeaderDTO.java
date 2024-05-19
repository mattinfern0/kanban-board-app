package io.mattinfern0.kanbanboardapi.boards.dtos;

import jakarta.validation.constraints.NotEmpty;

public record UpdateBoardHeaderDTO(
    @NotEmpty
    String title
) {
}
