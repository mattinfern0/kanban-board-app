package io.mattinfern0.kanbanboardapi.boards.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateBoardDto {
    @NotNull
    String title;

    @NotNull
    UUID organizationId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }
}
