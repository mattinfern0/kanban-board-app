package io.mattinfern0.kanbanboardapi.boards.dtos;

import io.mattinfern0.kanbanboardapi.core.constraints.EntityWithIdExists;
import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateBoardDto {
    @NotNull
    String title;

    @NotNull
    @EntityWithIdExists(entityClass = Organization.class)
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
