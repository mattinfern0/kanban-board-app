package io.mattinfern0.kanbanboardapi.core.entities;

import io.mattinfern0.kanbanboardapi.core.enums.TaskStatusCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.NaturalId;

import java.util.UUID;

@Entity
@Table(name = "task_status")
public class TaskStatus {
    @Id
    @GeneratedValue
    UUID id;


    @Enumerated(EnumType.STRING)
    @NotNull
    @NaturalId
    TaskStatusCode codename;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TaskStatusCode getCodename() {
        return codename;
    }

    public void setCodename(TaskStatusCode codename) {
        this.codename = codename;
    }
}
