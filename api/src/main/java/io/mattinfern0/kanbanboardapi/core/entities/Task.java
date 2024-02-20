package io.mattinfern0.kanbanboardapi.core.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "task")
public class Task {
    @Id
    UUID id;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    @NotNull
    Organization organization;

    @Column(name = "title")
    @NotNull
    String title;

    @Column(name = "description")
    @NotNull
    String description;
}
