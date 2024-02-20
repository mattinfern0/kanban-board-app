package io.mattinfern0.kanbanboardapi.core.dtos;

import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class TaskDto {
    UUID id;

    @NotNull
    UUID organizationId;

    @NotNull
    String title;

    @NotNull
    String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
