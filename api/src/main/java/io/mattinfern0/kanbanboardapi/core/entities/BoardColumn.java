package io.mattinfern0.kanbanboardapi.core.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "board_column")
public class BoardColumn {
    @Id
    UUID id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "board_id")
    Board board;

    @NotNull
    @Column(name = "title")
    String title;

    @NotNull
    @Min(0)
    @Column(name = "display_order")
    Integer displayOrder;
}
