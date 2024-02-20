package io.mattinfern0.kanbanboardapi.core.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "organization")
public class Organization {
    @Id
    UUID id;

    @NotNull
    @Column(name = "display_name")
    String displayName;
}
