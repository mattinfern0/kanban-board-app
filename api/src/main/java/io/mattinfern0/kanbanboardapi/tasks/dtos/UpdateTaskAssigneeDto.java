package io.mattinfern0.kanbanboardapi.tasks.dtos;

import io.mattinfern0.kanbanboardapi.core.constraints.EntityWithIdExists;
import io.mattinfern0.kanbanboardapi.core.entities.User;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class UpdateTaskAssigneeDto {
    @NotNull
    @EntityWithIdExists(entityClass = User.class)
    UUID userId;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
