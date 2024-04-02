package io.mattinfern0.kanbanboardapi.tasks.dtos;

import org.springframework.lang.Nullable;

import java.util.UUID;

public class TaskAssigneeDto {
    @Nullable
    UUID userId;

    @Nullable
    String firstName;

    @Nullable
    String lastName;

    @Nullable
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(@Nullable UUID userId) {
        this.userId = userId;
    }

    @Nullable
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(@Nullable String firstName) {
        this.firstName = firstName;
    }

    @Nullable
    public String getLastName() {
        return lastName;
    }

    public void setLastName(@Nullable String lastName) {
        this.lastName = lastName;
    }
}
