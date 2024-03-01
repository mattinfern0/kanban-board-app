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

    @ManyToOne
    @JoinColumn(name = "task_status_id")
    @NotNull
    TaskStatus taskStatus;

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

        if (newBoardColumn != null) {
            if (!newBoardColumn.getTasks().contains(this)) {
                newBoardColumn.getTasks().add(this);
            }

            this.setTaskStatus(newBoardColumn.getTaskStatus());
        }

        this.boardColumn = newBoardColumn;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {

        if (this.getBoardColumn() != null && !this.getBoardColumn().getTaskStatus().equals(taskStatus)) {
            throw new IllegalArgumentException(
                String.format(
                    "taskStatus %s is not consistent with column's status (%s)",
                    taskStatus,
                    this.getBoardColumn().getTaskStatus()
                )
            );
        }

        this.taskStatus = taskStatus;
    }
}
