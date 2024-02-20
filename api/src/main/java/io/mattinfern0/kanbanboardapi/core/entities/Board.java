package io.mattinfern0.kanbanboardapi.core.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "board")
public class Board {
    @Id
    UUID id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "organization_id")
    Organization organization;

    @NotNull
    @Column(name = "title")
    String title;

    @OneToMany(mappedBy = "board")
    List<BoardColumn> boardColumns;
}
