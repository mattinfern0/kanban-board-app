package io.mattinfern0.kanbanboardapi.core.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "board")
public class Board {
    @Id
    @GeneratedValue
    UUID id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "organization_id")
    Organization organization;

    @NotNull
    @Column(name = "title")
    String title;

    @OneToMany(mappedBy = "board")
    @OrderBy("displayOrder")
    List<BoardColumn> boardColumns;

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

    public List<BoardColumn> getBoardColumns() {
        return boardColumns;
    }

    public void setBoardColumns(List<BoardColumn> boardColumns) {
        this.boardColumns = boardColumns;
    }
}
