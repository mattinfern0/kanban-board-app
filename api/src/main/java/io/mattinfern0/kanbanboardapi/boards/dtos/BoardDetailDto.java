package io.mattinfern0.kanbanboardapi.boards.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public class BoardDetailDto {
    UUID id;

    @NotNull
    UUID organizationId;

    @NotNull
    String title;

    List<BoardColumnDto> boardColumns;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<BoardColumnDto> getBoardColumns() {
        return boardColumns;
    }

    public void setBoardColumns(List<BoardColumnDto> boardColumns) {
        this.boardColumns = boardColumns;
    }
}
