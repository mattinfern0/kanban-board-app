package io.mattinfern0.kanbanboardapi.core.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "board_column")
public class BoardColumn {
    @Id
    @GeneratedValue
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

    @OneToMany(mappedBy = "boardColumn")
    List<Task> tasks;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
