package io.mattinfern0.kanbanboardapi.core.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue
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

    @ManyToOne
    @JoinColumn(name = "board_column_id")
    BoardColumn boardColumn;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
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

    public BoardColumn getBoardColumn() {
        return boardColumn;
    }

    public void setBoardColumn(BoardColumn newBoardColumn) {
        if (this.boardColumn != null) {
            this.boardColumn.getTasks().remove(this);
        }

        if (newBoardColumn != null && !newBoardColumn.getTasks().contains(this)) {
            newBoardColumn.getTasks().add(this);
        }

        this.boardColumn = newBoardColumn;
    }
}
