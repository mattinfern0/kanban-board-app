package io.mattinfern0.kanbanboardapi.core.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "task",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"board_column_id", "board_column_order"})
    }
)
public class Task {
    @Id
    @GeneratedValue
    UUID id;

    @Column(name = "created_at")
    @CreationTimestamp
    ZonedDateTime createdAt;

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

    @Nullable
    @Column(name = "board_column_order")
    @Min(0)
    Integer boardColumnOrder;

    @ManyToOne
    @JoinColumn(name = "task_status_id")
    @NotNull
    TaskStatus taskStatus;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    @Nullable
    User assignee;

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
        this.boardColumn = newBoardColumn;
    }

    @Nullable
    public Integer getBoardColumnOrder() {
        return boardColumnOrder;
    }

    public void setBoardColumnOrder(@Nullable Integer boardColumnOrder) {
        this.boardColumnOrder = boardColumnOrder;
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

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Nullable
    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(@Nullable User assignee) {
        this.assignee = assignee;
    }
}
